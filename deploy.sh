#!/bin/bash
set -e

APP_NAME=finmate
TOMCAT_HOME=/home/ubuntu/tomcat
TOMCAT=$TOMCAT_HOME/tomcat-8081
NGINX_SITES=/etc/nginx/sites-available/$APP_NAME
PROJECT_DIR=/home/ubuntu/app/step1/FinMate-SERVER
CONFIG_DIR=$PROJECT_DIR/server-submodule/
WAR_NAME=FinMate-SERVER-1.0-SNAPSHOT.war

# 톰캣 lib에 로그 관련 JAR 복사 경로
LOG4J_JARS=(
    "$PROJECT_DIR/build/libs/log4j-api-2.24.1.jar"
    "$PROJECT_DIR/build/libs/log4j-core-2.24.1.jar"
    "$PROJECT_DIR/build/libs/log4j-slf4j-impl-2.24.1.jar"
    "$PROJECT_DIR/build/libs/log4jdbc-log4j2-jdbc4.1-1.16.jar"
)

cd $PROJECT_DIR

# 1. 환경 변수 로딩
set -o allexport
source $PROJECT_DIR/.env
set +o allexport
echo "[`date`] Loaded environment variables"

# 1.1 Spring 필수 config.location 환경 변수 설정 (디렉토리 경로로 설정)
export CONFIG_LOCATION=$CONFIG_DIR
echo "[`date`] CONFIG_LOCATION set to $CONFIG_LOCATION"

# 2. 톰캣 기본 디렉토리 생성
mkdir -p $TOMCAT/webapps
mkdir -p $TOMCAT/logs

# 2.1 톰캣 lib에 log4j JAR 복사
for jar in "${LOG4J_JARS[@]}"; do
    if [[ -f "$jar" ]]; then
        cp -u "$jar" "$TOMCAT/lib/"
        echo "[`date`] Copied $(basename $jar) to $TOMCAT/lib/"
    else
        echo "[WARN] JAR not found: $jar"
    fi
done

# 3. Gradle 빌드
chmod +x ./gradlew
echo "[`date`] Building project..."
./gradlew clean build -x test --parallel --configure-on-demand

# 4. WAR 배포 (ROOT.war)
WAR_PATH=$PROJECT_DIR/build/libs/$WAR_NAME
if [[ ! -f "$WAR_PATH" ]]; then
    echo "[ERROR] WAR file not found: $WAR_PATH"
    exit 1
fi

rm -rf $TOMCAT/webapps/ROOT
rm -f  $TOMCAT/webapps/ROOT.war
cp $WAR_PATH $TOMCAT/webapps/ROOT.war
echo "[`date`] Copied WAR as ROOT.war to Tomcat"

# 5. 톰캣 재시작
echo "[`date`] Restarting Tomcat..."
$TOMCAT/bin/shutdown.sh || true
sleep 5

# CATALINA_OPTS에 Log4j2 설정 추가 (config.location을 디렉토리로 설정)
export CATALINA_OPTS="-Dconfig.location=$CONFIG_DIR -Dlog4j.configurationFile=$PROJECT_DIR/src/main/resources/log4j2.xml"
echo "[`date`] CATALINA_OPTS: $CATALINA_OPTS"
cd $TOMCAT/bin
./startup.sh

# 5.1 톰캣 기동 확인 (개선된 버전)
echo "[`date`] Waiting for Tomcat to start on port 8081..."
TIMEOUT=60
ELAPSED=0
INTERVAL=3

while [ $ELAPSED -lt $TIMEOUT ]; do
    if nc -z localhost 8081; then
        echo "[`date`] ✅ Tomcat started successfully on port 8081 (after ${ELAPSED}s)"
        break
    fi
    echo "[`date`] ⏳ Waiting... (${ELAPSED}/${TIMEOUT}s)"
    sleep $INTERVAL
    ELAPSED=$((ELAPSED + INTERVAL))
done

if ! nc -z localhost 8081; then
    echo "[ERROR] ❌ Tomcat failed to start on 8081 after ${TIMEOUT} seconds"
    echo "[DEBUG] Last 20 lines of catalina.out:"
    tail -20 $TOMCAT/logs/catalina.out
    echo "[DEBUG] Checking if Tomcat process is running:"
    ps aux | grep java | grep tomcat || echo "No Tomcat process found"
    exit 1
fi

# 5.2 애플리케이션 응답 확인
echo "[`date`] Testing application response..."
if curl -f -s http://localhost:8081/ > /dev/null; then
    echo "[`date`] ✅ Application is responding"
else
    echo "[`date`] ⚠️  Application may still be loading, but Tomcat is running"
fi

# 6. Nginx reload (단일 서버용)
echo "[`date`] Reloading Nginx..."
sudo sed -i "s/server 127.0.0.1:808[12]/server 127.0.0.1:8081/" $NGINX_SITES
sudo nginx -s reload || echo "[WARN] Nginx reload failed, check config"

echo "[`date`] 🚀 Deployment finished successfully!"