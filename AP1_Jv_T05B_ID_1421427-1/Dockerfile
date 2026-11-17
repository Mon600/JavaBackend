# syntax=docker/dockerfile:1

ARG APP_DIR=src/TicTacToe



FROM gradle:8.14-jdk21 AS build
ARG APP_DIR
WORKDIR /home/gradle/project


COPY --chown=gradle:gradle ${APP_DIR}/settings.gradle.kts ${APP_DIR}/build.gradle.kts ./
RUN gradle --no-daemon dependencies --configuration runtimeClasspath > /dev/null

COPY --chown=gradle:gradle ${APP_DIR}/src ./src
RUN gradle --no-daemon bootJar \
    && cp "$(find build/libs -maxdepth 1 -name '*.jar' ! -name '*-plain.jar' | head -n 1)" app.jar


FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=build --chown=spring:spring /home/gradle/project/app.jar /app/app.jar

USER spring

# Параметры JVM переопределяются через переменную окружения JAVA_OPTS
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Spring Boot по умолчанию слушает 8080 (server.port в конфигах не переопределён)
EXPOSE 8080

# sh -c + exec: подстановка JAVA_OPTS и корректная доставка SIGTERM (graceful shutdown)
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
