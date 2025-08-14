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
if curl -s http://127.0.0.1:8081/health >/dev/null 2>&1; then
    ACTIVE_TOMCAT=$TOMCAT1
    STANDBY_TOMCAT=$TOMCAT2
    STANDBY_PORT=8082
else
    ACTIVE_TOMCAT=$TOMCAT2
    STANDBY_TOMCAT=$TOMCAT1
    STANDBY_PORT=8081
fi
echo "[`date`] Active Tomcat: $ACTIVE_TOMCAT, Standby Tomcat: $STANDBY_TOMCAT"

# 3. Gradle 빌드
chmod +x ./gradlew
echo "[`date`] Building project..."
./gradlew clean build -x test --parallel --configure-on-demand

# 4. WAR 배포 (ROOT.war로)
WAR_PATH=$PROJECT_DIR/build/libs/$WAR_NAME
if [[ ! -f "$WAR_PATH" ]]; then
    echo "[ERROR] WAR file not found: $WAR_PATH"
    exit 1
fi
cp $WAR_PATH $STANDBY_TOMCAT/webapps/ROOT.war
echo "[`date`] Copied WAR as ROOT.war to standby Tomcat"

# 5. 스탠바이 Tomcat 재시작
echo "[`date`] Restarting standby Tomcat..."
$STANDBY_TOMCAT/bin/shutdown.sh || true
$STANDBY_TOMCAT/bin/startup.sh

# 6. Nginx upstream 안전 전환
echo "[`date`] Switching Nginx upstream to $STANDBY_PORT..."
sudo sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:$STANDBY_PORT/" $NGINX_SITES
sudo nginx -s reload || echo "[WARN] Nginx reload failed, check config"

# 7. 이전 Tomcat 종료
if nc -z localhost 8005; then
    echo "[`date`] Shutting down active Tomcat..."
    $ACTIVE_TOMCAT/bin/shutdown.sh || echo "[WARN] Shutdown command failed"
else
    echo "[`date`] Active Tomcat not running on 8005, skip shutdown"
fi

echo "[`date`] Deployment finished successfully!"
