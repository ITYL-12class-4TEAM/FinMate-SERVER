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

# 2. 현재 활성/스탠바이 톰캣 결정
if curl -s http://127.0.0.1:8081/health >/dev/null 2>&1; then
    ACTIVE_TOMCAT=$TOMCAT1
    STANDBY_TOMCAT=$TOMCAT2
    STANDBY_PORT=8082
else
    ACTIVE_TOMCAT=$TOMCAT2
    STANDBY_TOMCAT=$TOMCAT1
    STANDBY_PORT=8081
fi

# 3. 빌드
echo "[`date`] Building project..."
chmod +x ./gradlew
./gradlew clean build -x test --parallel --configure-on-demand

# 4. WAR 배포
WAR_PATH=$PROJECT_DIR/build/libs/$WAR_NAME
if [[ ! -f "$WAR_PATH" ]]; then
    echo "WAR file not found: $WAR_PATH"
    exit 1
fi
cp $WAR_PATH $STANDBY_TOMCAT/webapps/$APP_NAME.war

# 5. 스탠바이 톰캣 재시작
$STANDBY_TOMCAT/bin/shutdown.sh || true
$STANDBY_TOMCAT/bin/startup.sh

# 6. Nginx upstream 전환 (블루그린)
sudo sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:$STANDBY_PORT/" $NGINX_SITES
sudo nginx -t
sudo systemctl reload nginx || true

# 7. 이전 톰캣 종료
$ACTIVE_TOMCAT/bin/shutdown.sh || true

echo "[`date`] Deployment finished successfully!"