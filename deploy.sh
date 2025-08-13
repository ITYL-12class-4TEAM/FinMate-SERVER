#!/bin/bash
# ----------------------------
# FinMate 무중단 배포 스크립트
# ----------------------------

set -e

APP_NAME=finmate
TOMCAT_HOME=/home/tomcat
TOMCAT1=$TOMCAT_HOME/tomcat-8081
TOMCAT2=$TOMCAT_HOME/tomcat-8082
NGINX_SITES=/etc/nginx/sites-available/$APP_NAME

# 1. 환경 변수 로딩
export $(grep -v '^#' /home/ubuntu/app/step1/FinMate-SERVER/.env | xargs)

echo "[$(date)] Loaded environment variables"

# 2. 현재 활성 Tomcat 확인
ACTIVE_PORT=$(curl -s http://127.0.0.1:8080/health || echo "8081")
if [[ "$ACTIVE_PORT" == "8081" ]]; then
    ACTIVE_TOMCAT=$TOMCAT1
    STANDBY_TOMCAT=$TOMCAT2
else
    ACTIVE_TOMCAT=$TOMCAT2
    STANDBY_TOMCAT=$TOMCAT1
fi

echo "[$(date)] Active Tomcat: $ACTIVE_TOMCAT"

# 3. 새로운 WAR 복사
cp /home/ubuntu/app/step1/FinMate-SERVER/build/libs/$APP_NAME.war $STANDBY_TOMCAT/webapps/$APP_NAME.war
echo "[$(date)] WAR copied to standby Tomcat"

# 4. 스탠바이 Tomcat 시작
$STANDBY_TOMCAT/bin/shutdown.sh || true
$STANDBY_TOMCAT/bin/startup.sh
echo "[$(date)] Standby Tomcat started"

# 5. Nginx upstream 전환
sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:$( [[ $ACTIVE_PORT == 8081 ]] && echo 8082 || echo 8081)/" $NGINX_SITES
sudo nginx -s reload
echo "[$(date)] Nginx reloaded"

# 6. 이전 Tomcat 종료
$ACTIVE_TOMCAT/bin/shutdown.sh
echo "[$(date)] Old Tomcat stopped"

echo "[$(date)] Deployment finished successfully!"
