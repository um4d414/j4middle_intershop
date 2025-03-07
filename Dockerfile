# Stage 1: Сборка проекта с использованием OpenJDK 21 и Gradle Wrapper
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
# Копируем все файлы проекта
COPY . .
# Делаем gradlew исполняемым
RUN chmod +x gradlew
# Собираем jar-файл
RUN ./gradlew clean bootJar

# Stage 2: Формирование финального образа для запуска приложения
FROM openjdk:21-jdk-slim
WORKDIR /app
# Копируем собранный jar-файл из stage 1
COPY --from=builder /app/build/libs/intershop-1.0.jar app.jar
EXPOSE 8080
RUN chmod +x app.jar
ENTRYPOINT ["./app.jar"]