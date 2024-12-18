#!/bin/bash

set -e

# 기본 경로 설정
APP_HOME=/home/ec2-user/app/interstory
LOG_DIR=$APP_HOME/log
DEPLOY_PATH=$APP_HOME

# 로그 디렉토리 생성
mkdir -p $LOG_DIR

# 로그 파일 경로
LOG_PATH=$LOG_DIR/spring-deploy.out
ERROR_LOG_PATH=$LOG_DIR/spring-deploy_err.out


echo "### Deployment started at $(date)" >> $LOG_PATH

# Docker 설치 확인 및 설치
if ! command -v docker &> /dev/null; then
    echo "## Docker 설치를 시작합니다." >> $LOG_PATH
    sudo yum update -y
    sudo yum install -y docker
    sudo service docker start
    sudo usermod -a -G docker ec2-user
    echo "### Docker installed successfully" >> $LOG_PATH
fi

# Docker Compose 설치
if ! command -v docker-compose &> /dev/null; then
    echo "## Docker Compose 설치를 시작합니다." >> $LOG_PATH
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose  # 심볼릭 링크 생성
    echo "### Docker Compose installed successfully" >> $LOG_PATH
fi

# 파일 권한 설정
sudo chown -R ec2-user:ec2-user $APP_HOME
sudo chmod -R 755 $APP_HOME

# Redis 설정 파일 생성
if [ ! -f "$APP_HOME/redis.conf" ]; then
    sudo bash -c "cat > $APP_HOME/redis.conf << 'EOL'
bind 0.0.0.0
port 6379
maxmemory 256mb
maxmemory-policy allkeys-lru
appendonly yes
EOL"
fi

echo "### Setting up Redis configuration..." >> $LOG_PATH

# Redis 컨테이너 실행
echo "## Redis 컨테이너 시작" >> $LOG_PATH
if [ ! -f "$APP_HOME/docker-compose.yml" ]; then
    cat > $APP_HOME/docker-compose.yml << EOL
version: '3.8'
services:
  redis:
    container_name: interstory-redis
    image: redis:latest
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    restart: always
    networks:
      - interstory-network

volumes:
  redis_data:
    driver: local

networks:
  interstory-network:
    driver: bridge
EOL
fi

# 기존 Redis 컨테이너 중지 및 재시작
sudo /usr/local/bin/docker-compose -f $APP_HOME/docker-compose.yml down
sudo /usr/local/bin/docker-compose -f $APP_HOME/docker-compose.yml up -d

# JVM 옵션 설정
JAVA_OPTS="-Dspring.profiles.active=prod -Dserver.port=8080 -Xms512m -Xmx1024m"

# SNAPSHOT.jar 파일 찾기 및 검증
echo "## Checking for JAR files in $DEPLOY_PATH" >> $LOG_PATH
BUILD_JAR=$(find $DEPLOY_PATH -name "*SNAPSHOT.jar" -type f)

if [ -z "$BUILD_JAR" ]; then
    echo "### Error: No JAR file found in $DEPLOY_PATH" >> $LOG_PATH
    ls -l $DEPLOY_PATH >> $LOG_PATH
    exit 1
fi

# 여러 JAR 파일이 있는 경우 처리
if [ $(echo "$BUILD_JAR" | wc -l) -gt 1 ]; then
    echo "### Multiple JAR files found, using the latest one" >> $LOG_PATH
    BUILD_JAR=$(ls -t $DEPLOY_PATH/*SNAPSHOT.jar | head -1)
fi

# JAR 파일 무결성 검사
echo "## Verifying JAR file integrity" >> $LOG_PATH
if ! jar tf "$BUILD_JAR" > /dev/null 2>&1; then
    echo "### Error: Corrupt JAR file: $BUILD_JAR" >> $LOG_PATH
    exit 1
fi

JAR_NAME=$(basename $BUILD_JAR)
echo "## Found valid JAR file: $JAR_NAME" >> $LOG_PATH

# JAR 파일 권한 확인 및 설정
echo "## Setting JAR file permissions" >> $LOG_PATH
sudo chown ec2-user:ec2-user "$BUILD_JAR"
sudo chmod 755 "$BUILD_JAR"

# 실행 경로 및 JAR 파일 존재 확인
DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME
if [ ! -f "$DEPLOY_JAR" ]; then
    echo "### Error: JAR file not found at $DEPLOY_JAR" >> $LOG_PATH
    exit 1
fi

echo "## Deploying JAR file: $DEPLOY_JAR" >> $LOG_PATH

echo "## Checking current process" >> $LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
    echo "## No running application found" >> $LOG_PATH
else
    echo "## Terminating existing application (PID: $CURRENT_PID)" >> $LOG_PATH
    kill -15 $CURRENT_PID
    sleep 5

    # 프로세스가 종료되었는지 확인
    if kill -0 $CURRENT_PID 2>/dev/null; then
        echo "## Force terminating application" >> $LOG_PATH
        kill -9 $CURRENT_PID
    fi
fi

# 새로운 애플리케이션 실행
echo "## Starting new application" >> $LOG_PATH

# 환경 변수 확인
echo "## Checking environment variables" >> $LOG_PATH
if [ ! -f "$APP_HOME/.env.properties" ]; then
    echo "### Error: .env.properties file not found" >> $LOG_PATH
    exit 1
fi

# Java 명령어 실행 시 환경 변수 파일 명시적 지정
JAVA_OPTS="$JAVA_OPTS -Dspring.config.import=file:${APP_HOME}/.env.properties"

# 애플리케이션 실행
cd $APP_HOME  # 작업 디렉토리 변경
echo "## Executing: java $JAVA_OPTS -jar $DEPLOY_JAR" >> $LOG_PATH

MAX_WAIT=30
echo "## Starting application... (waiting max ${MAX_WAIT}s)" >> $LOG_PATH

# 실행 명령어 추가
#nohup java $JAVA_OPTS -jar $DEPLOY_JAR >> $LOG_PATH 2>> $ERROR_LOG_PATH &
nohup java -jar $DEPLOY_JAR >> $LOG_PATH 2>> $ERROR_LOG_PATH &

# 실행 확인 (타임아웃 추가)
for i in $(seq 1 $MAX_WAIT); do
   sleep 1
   NEW_PID=$(pgrep -f $JAR_NAME)
   if [ -n "$NEW_PID" ]; then
       echo "## Application started successfully (PID: $NEW_PID)" >> $LOG_PATH
       echo $NEW_PID > $APP_HOME/current.pid
       exit 0  # 성공적으로 실행되면 스크립트 종료
   fi
done

# 타임아웃된 경우
echo "## Application failed to start within ${MAX_WAIT} seconds" >> $LOG_PATH
echo "## Checking error logs:" >> $LOG_PATH
tail -n 50 $ERROR_LOG_PATH >> $LOG_PATH
exit 1