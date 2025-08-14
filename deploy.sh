#!/bin/bash
set -e

APP_NAME=finmate
TOMCAT_HOME=/home/ubuntu/tomcat
TOMCAT1=$TOMCAT_HOME/tomcat-8081
TOMCAT2=$TOMCAT_HOME/tomcat-8082
NGINX_SITES=/etc/nginx/sites-available/$APP_NAME
PROJECT_DIR=/home/ubuntu/app/step1/FinMate-SERVER

cd $PROJECT_DIR

# 1. 환경 변수 로딩
set -o allexport
source $PROJECT_DIR/.env
set +o allexport
echo "[`date`] Loaded environment variables"

# 2. 현재 활성 Tomcat 확인
ACTIVE_PORT=$(curl -s http://127.0.0.1:8080/health || echo "8081")
if [[ "$ACTIVE_PORT" == "8081" ]]; then
    ACTIVE_TOMCAT=$TOMCAT1
    STANDBY_TOMCAT=$TOMCAT2
else
    ACTIVE_TOMCAT=$TOMCAT2
    STANDBY_TOMCAT=$TOMCAT1
fi

# 3. 빌드
echo "[`date`] Building project..."
./gradlew clean build -x test --parallel --configure-on-demand

# 4. WAR 배포
WAR_PATH=$PROJECT_DIR/build/libs/$WAR_NAME
if [[ ! -f "$WAR_PATH" ]]; then
    echo "WAR file not found: $WAR_PATH"
    exit 1
fi
cp $WAR_PATH $STANDBY_TOMCAT/webapps/$APP_NAME.war

# 5. 스탠바이 Tomcat 재시작
$STANDBY_TOMCAT/bin/shutdown.sh || true
$STANDBY_TOMCAT/bin/startup.sh

# 6. Nginx upstream 전환 (에러 무시)
if [ -f /run/nginx.pid ]; then
    sudo nginx -s reload || true
else
    sudo nginx || true
fi
sudo sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:$( [[ $ACTIVE_PORT == 8081 ]] && echo 8082 || echo 8081)/" $NGINX_SITES || true

# 7. 이전 Tomcat 종료
$ACTIVE_TOMCAT/bin/shutdown.sh || true

echo "[`date`] Deployment finished successfully!"
