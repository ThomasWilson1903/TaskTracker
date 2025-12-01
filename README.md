# TaskTracker

    Решение об использовании сервиса принимаете только вы.

# Запуск приложения

## Docker Compose

Для запуска в фоновом режиме выполните:

```bash
  docker-compose up -d --build
```

Для остановки приложения:

```bash
  docker-compose down
```

Для остановки с удалением томов (volumes):

```bash
  docker-compose down -v
```

# Документация API (Swagger)

Документация доступна по адресу:

http://localhost:8081/swagger-ui/index.html