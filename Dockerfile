FROM openjdk:17-slim

# 작업 디렉터리 설정
WORKDIR /app

# Spring 소스 코드를 이미지에 복사
COPY . .

# /root/.gradle 디렉토리 생성
RUN mkdir -p /root/.gradle

# gradle 빌드 시 proxy 설정을 gradle.properties에 추가
RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# gradlew를 이용한 프로젝트 빌드
RUN ./gradlew clean build

# DATABASE_URL을 환경 변수로 삽입
ENV DATABASE_URL=jdbc:mariadb://mariadb/krampoline

# 빌드 결과 jar 파일을 실행
CMD ["java", "-jar", "/app/build/libs/ssap-0.0.1-SNAPSHOT.jar"]