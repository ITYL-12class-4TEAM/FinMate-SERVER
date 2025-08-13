#!/bin/bash
set -e

APP_NAME=finmate
TOMCAT_HOME=/home/ubuntu/tomcat
TOMCAT1=$TOMCAT_HOME/tomcat-8081
TOMCAT2=$TOMCAT_HOME/tomcat-8082
NGINX_SITES=/etc/nginx/sites-available/$APP_NAME

# 환경 변수 로딩
set -o allexport
source /home/ubuntu/app/step1/FinMate-SERVER/.env
set +o allexport

echo "[`date`] Loaded environment variables"
# 현재 활성 Tomcat 확인
ACTIVE_PORT=$(curl -s http://127.0.0.1:8080/health || echo "8081")
if [[ "$ACTIVE_PORT" == "8081" ]]; then
    ACTIVE_TOMCAT=$TOMCAT1
    STANDBY_TOMCAT=$TOMCAT2
else
    ACTIVE_TOMCAT=$TOMCAT2
    STANDBY_TOMCAT=$TOMCAT1
fi

# 새로운 WAR 배포
cp /home/ubuntu/app/step1/FinMate-SERVER/build/libs/FinMate-SERVER-1.0-SNAPSHOT.war \
   $STANDBY_TOMCAT/webapps/$APP_NAME.war

# 스탠바이 Tomcat 시작
$STANDBY_TOMCAT/bin/shutdown.sh || true
$STANDBY_TOMCAT/bin/startup.sh

# Nginx upstream 전환
sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:$( [[ $ACTIVE_PORT == 8081 ]] && echo 8082 || echo 8081)/" $NGINX_SITES
sudo nginx -s reload

# 이전 Tomcat 종료
$ACTIVE_TOMCAT/bin/shutdown.sh

echo "Deployment finished successfully!"
