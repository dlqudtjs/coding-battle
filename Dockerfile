# 단계 1: 빌드 단계
FROM gradle:7.5.1-jdk17 AS build

# 패키지 목록 업데이트 및 dos2unix 설치
RUN rm -rf /var/lib/apt/lists/*
RUN apt-get clean
RUN apt-get update && apt-get install -y dos2unix

WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN dos2unix gradlew
RUN ./gradlew clean build
RUN ls -al build/libs

# 단계 2: 실행 단계
FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/build/libs/codingbattle-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
