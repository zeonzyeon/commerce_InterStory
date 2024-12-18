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

# 디버그 로깅 함수
debug_log() {
    echo "[DEBUG] $1" >> $LOG_PATH
}

debug_log "### Deployment started at $(date)"

# Java 버전 확인
debug_log "## Java Version Check"
java -version >> $LOG_PATH 2>&1

# SNAPSHOT.jar 파일 찾기 및 검증
debug_log "## Checking for JAR files in $DEPLOY_PATH"
BUILD_JAR=$(find $DEPLOY_PATH -name "*SNAPSHOT.jar" -type f | grep -v "plain.jar")

if [ -z "$BUILD_JAR" ]; then
    debug_log "### Error: No JAR file found in $DEPLOY_PATH"
    ls -l $DEPLOY_PATH >> $LOG_PATH
    exit 1
fi

# 여러 JAR 파일이 있는 경우 처리
if [ $(echo "$BUILD_JAR" | wc -l) -gt 1 ]; then
    debug_log "### Multiple JAR files found, using the latest one"
    BUILD_JAR=$(ls -t $DEPLOY_PATH/*SNAPSHOT.jar | grep -v "plain.jar" | head -1)
fi

debug_log "## Found JAR file: $BUILD_JAR"

# JAR 파일 세부 정보 확인
debug_log "## JAR File Details"
ls -l "$BUILD_JAR" >> $LOG_PATH
file "$BUILD_JAR" >> $LOG_PATH

# JAR 파일 무결성 검사
debug_log "## Verifying JAR file integrity"
if ! jar tf "$BUILD_JAR" > /dev/null 2>&1; then
    debug_log "### Error: Corrupt JAR file: $BUILD_JAR"
    exit 1
fi

JAR_NAME=$(basename "$BUILD_JAR")
debug_log "## Found valid JAR file: $JAR_NAME"

# JAR 파일 권한 확인 및 설정
debug_log "## Setting JAR file permissions"
sudo chown ec2-user:ec2-user "$BUILD_JAR"
sudo chmod 755 "$BUILD_JAR"

# 실행 경로 및 JAR 파일 존재 확인
DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME
if [ ! -f "$DEPLOY_JAR" ]; then
    debug_log "### Error: JAR file not found at $DEPLOY_JAR"
    exit 1
fi

debug_log "## Deploying JAR file: $DEPLOY_JAR"

# 현재 실행 중인 프로세스 확인
debug_log "## Checking current process"
CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]; then
    debug_log "## No running application found"
else
    debug_log "## Terminating existing application (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5

    # 프로세스가 종료되었는지 확인
    if kill -0 $CURRENT_PID 2>/dev/null; then
        debug_log "## Force terminating application"
        kill -9 $CURRENT_PID
    fi
fi

# 환경 변수 및 설정 파일 확인
debug_log "## Checking .env.properties"
if [ ! -f "$APP_HOME/.env.properties" ]; then
    debug_log "### Warning: .env.properties file not found"
fi

# 새로운 애플리케이션 실행
debug_log "## Starting new application"

# JVM 옵션 설정
JAVA_OPTS="-Dserver.port=8080 -Xms512m -Xmx1024m"

# 애플리케이션 실행
cd $APP_HOME  # 작업 디렉토리 변경
debug_log "## Executing: java $JAVA_OPTS -jar $DEPLOY_JAR"

MAX_WAIT=60
debug_log "## Starting application... (waiting max ${MAX_WAIT}s)"

# 실행 명령어 (환경 변수 파일 옵션 제거)
nohup java $JAVA_OPTS -jar "$DEPLOY_JAR" >> $LOG_PATH 2>> $ERROR_LOG_PATH &

# 실행 확인 (타임아웃 추가)
for i in $(seq 1 $MAX_WAIT); do
   sleep 1
   NEW_PID=$(pgrep -f "$JAR_NAME")
   if [ -n "$NEW_PID" ]; then
       debug_log "## Application started successfully (PID: $NEW_PID)"
       echo $NEW_PID > $APP_HOME/current.pid
       exit 0  # 성공적으로 실행되면 스크립트 종료
   fi
done

# 타임아웃된 경우
debug_log "## Application failed to start within ${MAX_WAIT} seconds"
debug_log "## Checking error logs:"
tail -n 50 $ERROR_LOG_PATH >> $LOG_PATH
exit 1