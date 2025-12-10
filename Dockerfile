# Стадия сборки
FROM maven:sapmachine AS builder
WORKDIR /app
# Копируем файлы проекта
COPY ./pom.xml .
COPY ./src ./src
# Собираем проект
RUN mvn clean install -DskipTests

FROM openjdk:26-ea-17-jdk
WORKDIR /app

# Копируем собранный JAR из стадии сборки
COPY --from=builder /app/target/*.jar app.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "app.jar"]