# 단계 1: 빌드 단계
FROM gradle:7.5.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build

# 단계 2: 실행 단계
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app /app
COPY --from=build /app/build/libs/codingbattle-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
