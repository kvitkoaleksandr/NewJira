#  NewJira — Система управления задачами

NewJira — backend-приложение для управления задачами (по типу Jira), реализованное на Java 17 с использованием Spring Boot, Spring Security, PostgreSQL, Redis, JWT, Swagger UI.

##  Описание

API обеспечивает:
- автентификацию и авторизацию с помощью JWT
- ролевую модель: ADMIN и USER
- операции с задачами: создание, редактирование, удаление, фильтрация, пагинация
- комментарии к задачам
- Swagger UI для удобного тестирования API

##  Используемые технологии

- **Java 17** — язык программирования
- **Spring Boot** — backend-фреймворк
- **Spring Security** — аутентификация и авторизация
- **JWT (JSON Web Token)** — механизм аутентификации
- **PostgreSQL** — база данных
- **Liquibase** — управление схемой БД
- **Redis** — caching, настроен и отлаживается
- **MapStruct** — маппинг DTO в Entity
- **Swagger / OpenAPI** — документирование API
- **Docker & Docker Compose** — контейнеризация
- **JUnit 5 + Mockito** — юнит-тесты
- **Gradle Kotlin DSL** — система сборки

##  Функциональности

- Аутентификация по email/паролю + JWT
- Роли пользователей (USER / ADMIN)
- ADMIN:
    - Создание/удаление/редактирование задач
    - Назначение исполнителя
- USER:
    - Обновление статуса если является исполнителем
- Комментарии к задачам
- Фильтрация и пагинация задач
- Глобальная обработка ошибок (GlobalExceptionHandler)
- Экземплы Swagger:
    - http://localhost:8080/swagger-ui.html

##  Запуск проекта (локально)

### 1. Пререквизиты:
- Установлен Docker и Docker Desktop
- Установлена IntelliJ IDEA (JDK 17/19)

### 2. Клонируем репозиторий:
```bash
git clone https://github.com/your-username/NewJira.git
cd NewJira
```

### 3. Запускаем инфраструктуру:
```bash
docker-compose up --build
```

### 4. Запускаем Spring Boot:
- Через IntelliJ (класс `NewJira` → правой кнопкой → Run)

### 5. Swagger UI:
```http
http://localhost:8080/swagger-ui.html
```

##  Структура проекта

```
newJira/
├── controller/       # REST-контроллеры
├── service/          # Сервисный слой (бизнес-логика)
├── security/         # JWT, фильтры, конфигурации
├── entity/           # JPA-сущности
├── repository/       # Spring Data JPA
├── dto/              # DTO-объекты
├── mapper/           # MapStruct мапперы
├── exception/        # Кастомные исключения и GlobalExceptionHandler
├── config/           # Redis и др.
├── swagger/          # Swagger конфигурация
├── resources/        # application.yml и Liquibase
└── docker-compose.yml / Dockerfile
```

##  Тестирование

- Используются JUnit5 + Mockito
- Покрыты:
    - `AuthService`, `TaskService`, `CommentService`
    - `JwtTokenFilter`, `JwtTokenProvider`, `RoleChecker`
    - `CustomUserDetailsService`
    - `CommentMapper`, `TaskMapper`, `UserMapper`
    - `GlobalExceptionHandler`


##  Возможности для масштабирования и улучшения

### Масштабирование через микросервисы
- Проект легко масштабируется и может быть расширен до полноценной **микросервисной архитектуры**:
- Выделение сервисов: `Auth`, `Task`, `User`, `Comment`
- Связь между сервисами через **REST**, **gRPC** или **Kafka**
- Независимое масштабирование сервисов по нагрузке

### Kafka и брокеры сообщений
- Внедрение Kafka как централизованного брокера событий
- Использование для:
- логирования действий пользователей
- событий задач (создание, обновление, удаление)
- асинхронных уведомлений и интеграций

### Notification-сервис
- Вынос логики уведомлений в отдельный сервис
- Email / SMS / WebSocket уведомления при изменениях задач

### Мониторинг и метрики
- Интеграция с **Prometheus + Grafana**
- Использование **Spring Boot Actuator** для сбора технических метрик

### Web UI (в перспективе)
- Добавление фронтенда на **React** или **Vue**
- Авторизация через JWT + cookie / headers

---

##  Что можно улучшить в текущей архитектуре

### Оптимизация логики обновления задач
- Использовать `BeanUtils.copyProperties` или маппер DTO → Entity

### Улучшение работы Redis-кеша
- Кэшировать часто используемые запросы `findAll`, `getById`
- Добавить `@CacheEvict` для очистки при обновлении / удалении

### Использование Optional
- Метод `findByEmail` и другие — возвращать `Optional<AppUser>`
- Явная проверка отсутствия данных без `null`

### RoleChecker → в фильтр или интерцептор
- Вынести проверку ролей из сервисов/контроллеров в middleware слой

### JWT: refresh tokens
- Добавить механизм обновления токена без повторной авторизации
- Повышение UX + безопасность

### Интеграционные тесты (Testcontainers)
- Добавить полноценные E2E-тесты с PostgreSQL и Redis
- Контейнеры поднимаются вместе с тестами

### Валидация бизнес-логики
- Добавить кастомные валидаторы DTO
- Проверка валидности `status`, `priority`, `role`

### Dockerfile / CI
- Вынести `application.yml` внутрь jar на этапе сборки
- Добавить health-check команду
- Возможность деплоя в облачные платформы (например, Render, Fly.io)

---

> 🔗 Благодаря модульной структуре, проект может быть адаптирован для real-world production-систем.

##  Redis (статус: отлаживается)
Redis настроен и подключён, кэшируются:
- `getTasksByAuthor`, `getTasksByExecutors`
- `getCommentsByTaskId`

Ведётся отладка, кеш готов к использованию и при необходимости может быть расширен.

---

> Система NewJira создана по принципам SOLID, с трёхслойной архитектурой, код масштабируем и легко тестируем.
