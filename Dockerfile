# Стадия сборки
FROM maven:sapmachine AS builder
WORKDIR /app
# Копируем файлы проекта
COPY ../pom.xml .
COPY ../src ./src
# Собираем проект
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

# Копируем собранный JAR из стадии сборки
COPY --from=builder /app/target/*.jar app.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "app.jar"]