#!/bin/bash
set -e

APP_NAME=finmate
TOMCAT_HOME=/home/ubuntu/tomcat
TOMCAT=$TOMCAT_HOME/tomcat-8081
NGINX_SITES=/etc/nginx/sites-available/$APP_NAME
PROJECT_DIR=/home/ubuntu/app/step1/FinMate-SERVER
CONFIG_FILE=$PROJECT_DIR/server-submodule/application-prod.properties

cd $PROJECT_DIR

# 1. 환경 변수 로딩
set -o allexport
source $PROJECT_DIR/.env
set +o allexport
echo "[`date`] Loaded environment variables"

# 1.1 Spring 필수 config.location 환경 변수 설정
export CONFIG_LOCATION=$CONFIG_FILE
echo "[`date`] CONFIG_LOCATION set to $CONFIG_LOCATION"

# 2. Tomcat 기본 디렉토리 생성
mkdir -p $TOMCAT/webapps
mkdir -p $TOMCAT/logs

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

# 기존 ROOT 제거
rm -rf $TOMCAT/webapps/ROOT
rm -f  $TOMCAT/webapps/ROOT.war

cp $WAR_PATH $TOMCAT/webapps/ROOT.war
echo "[`date`] Copied WAR as ROOT.war to Tomcat"

# 5. Tomcat 재시작
echo "[`date`] Restarting Tomcat..."
$TOMCAT/bin/shutdown.sh || true
$TOMCAT/bin/startup.sh

# 5.1 기동 확인
sleep 5
if ! nc -z localhost 8081; then
    echo "[ERROR] Tomcat failed to start on 8081"
    exit 1
fi

# 6. Nginx reload (단일 서버용)
echo "[`date`] Reloading Nginx..."
sudo sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:8081/" $NGINX_SITES
sudo nginx -s reload || echo "[WARN] Nginx reload failed, check config"

echo "[`date`] Deployment finished successfully!"
