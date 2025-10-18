# TaskTracker

Нужно ли он тебе, решаешь только ты


# Запуск приложения

## Docker Compose

Сборка и запуск:
```bash
  docker-compose up --build
```

Или для фонового режима:
```bash
  docker-compose up -d --build
```

Просмотр логов:
```bash
  docker-compose logs
```

Остановка:
```bash
  docker-compose down
```

Остановка с удалением volumes:
```bash
  docker-compose down -v
```