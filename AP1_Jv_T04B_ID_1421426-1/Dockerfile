# syntax=docker/dockerfile:1

# Контекст сборки — эта папка (AP1_Jv_T04B_ID_1421426-1); Gradle-проект лежит в src/TicTacToe.
ARG APP_DIR=src/TicTacToe

# ---------------------------------------------------------------------------
# Stage 1 — сборка исполняемого jar (Spring Boot bootJar)
# JDK 21 (в IDE-настройках проекта — Temurin 21), Gradle 8.14 из образа
# (версия совпадает с gradle-wrapper.properties).
# bootJar доступен благодаря плагину org.springframework.boot в build.gradle.kts.
# ---------------------------------------------------------------------------
FROM gradle:8.14-jdk21 AS build
ARG APP_DIR
WORKDIR /home/gradle/project

# Слой с зависимостями: перекачивается только при изменении build-скриптов
COPY --chown=gradle:gradle ${APP_DIR}/settings.gradle.kts ${APP_DIR}/build.gradle.kts ./
RUN gradle --no-daemon dependencies --configuration runtimeClasspath > /dev/null

# Исходники и сборка
COPY --chown=gradle:gradle ${APP_DIR}/src ./src
RUN gradle --no-daemon bootJar \
    && cp "$(find build/libs -maxdepth 1 -name '*.jar' ! -name '*-plain.jar' | head -n 1)" app.jar

# ---------------------------------------------------------------------------
# Stage 2 — лёгкий runtime-образ (только JRE 21, без Gradle и исходников)
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=build --chown=spring:spring /home/gradle/project/app.jar /app/app.jar

USER spring

# Параметры JVM переопределяются через переменную окружения JAVA_OPTS
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Spring Boot по умолчанию слушает 8080 (server.port в конфигах не переопределён)
EXPOSE 8080

# sh -c + exec: подстановка JAVA_OPTS и корректная доставка SIGTERM
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
