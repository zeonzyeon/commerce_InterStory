#!/bin/bash

set -e

# 기본 경로 설정
APP_HOME=/home/ec2-user/app/interstory
LOG_DIR=$APP_HOME/log
DEPLOY_PATH=$APP_HOME

# 로그 디렉토리 생성
mkdir -p $LOG_DIR

# 로그 파일 경로
LOG_PATH=$LOG_DIR/spring-deploy.log
ERROR_LOG_PATH=$LOG_DIR/spring-deploy_err.log


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

# SNAPSHOT.jar 파일 찾기
BUILD_JAR=$(ls $DEPLOY_PATH/*SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)

echo "## build file name : $JAR_NAME" >> $LOG_PATH

#echo "## copy build file" >> $LOG_PATH
#cp $BUILD_JAR $DEPLOY_PATH

echo "## current pid" >> $LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z "$CURRENT_PID" ]; then
   echo "## 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> $LOG_PATH
else
   echo "## kill -15 $CURRENT_PID" >> $LOG_PATH
   kill -15 $CURRENT_PID
   sleep 5

   # 프로세스가 종료되었는지 확인
   if kill -0 $CURRENT_PID 2>/dev/null; then
       echo "## Force kill $CURRENT_PID" >> $LOG_PATH
       kill -9 $CURRENT_PID
   fi
fi

DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME
echo "## deploy JAR file" >> $LOG_PATH

# 새로운 애플리케이션 배포
echo "## $JAR_NAME 배포" >> $LOG_PATH
nohup java $JAVA_OPTS -jar $DEPLOY_JAR >> $LOG_PATH 2>> $ERROR_LOG_PATH &

# 배포 성공 여부 확인
sleep 10
NEW_PID=$(pgrep -f $JAR_NAME)
if [ -n "$NEW_PID" ]; then
   echo "## Deploy Success! New PID: $NEW_PID" >> $LOG_PATH
else
   echo "## Deploy Failed!" >> $LOG_PATH
   exit 1
fi

# 현재 실행 중인 애플리케이션 pid 기록
echo $NEW_PID > $APP_HOME/current.pid