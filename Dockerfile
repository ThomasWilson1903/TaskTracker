# Используем официальный образ OpenJDK
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Определяем аргумент для имени JAR-файла
ARG JAR_FILE=./target/*.jar

# Копируем файл JAR в контейнер
COPY ${JAR_FILE} app.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "app.jar"]
