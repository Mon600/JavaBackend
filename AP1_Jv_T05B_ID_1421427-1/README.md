# TicTacToe Backend — T05B (JWT + Flyway + лидерборд)

REST-бэкенд онлайн-игры «Крестики-нолики» на Spring Boot: регистрация и JWT-аутентификация, игра против компьютера (minimax) или против другого зарегистрированного игрока, история партий и таблица лидеров. В комплекте одностраничный веб-клиент [`src/game.html`](src/game.html). Окружение (API + PostgreSQL) поднимается одной командой через Docker Compose.

> Это актуальная версия сервиса. Предыдущая итерация (Basic-аутентификация, без миграций) лежит в соседней папке [`../AP1_Jv_T04B_ID_1421426-1`](../AP1_Jv_T04B_ID_1421426-1) и запускается отдельно.

## Стек

| Область | Технология | Версия (из `build.gradle.kts` / конфигов) |
|---|---|---|
| Язык / JDK | Java | 21 (Temurin 21; в коде используется `List.getLast()`) |
| Сборка | Gradle (wrapper-конфиг) | 8.14 |
| Фреймворк | Spring Boot (plugin + starters) | 4.1.0 |
| Web | `spring-boot-starter-web` | 4.1.0 |
| Persistence | `spring-boot-starter-data-jpa` (Hibernate, Spring Data JPA) | 4.1.0 |
| Безопасность | `spring-boot-starter-security` (BCrypt, stateless-сессии) | 4.1.0 |
| JWT | `io.jsonwebtoken:jjwt` | 0.13.0 |
| Миграции | `spring-boot-starter-flyway` + `flyway-database-postgresql` | 4.1.0 / 13.2.0 |
| Валидация | `jakarta.validation:jakarta.validation-api` | 4.0.0-M1 |
| БД и драйвер | PostgreSQL JDBC | 42.7.12 |
| Boilerplate | Lombok | 1.18.42 |
| Управление зависимостями | `io.spring.dependency-management` | 1.1.7 |
| Форматирование | Spotless (google-java-format, стиль AOSP) | 6.25.0 |
| Тесты (зависимости) | JUnit BOM / Jupiter | 5.10.0 |
| Инфраструктура (Docker) | PostgreSQL | 16-alpine |

## Быстрый старт (one-command run)

Нужны Docker с Compose v2. Сборка происходит внутри контейнера, локальные JDK и Gradle не требуются.

```bash
cd AP1_Jv_T05B_ID_1421427-1
docker compose up --build
```

После запуска:

| Что | Адрес |
|---|---|
| REST API | http://localhost:8080 |
| PostgreSQL (для psql / IDE) | `localhost:15432`, БД `tictactoe`, пользователь `tictactoe` |
| Веб-клиент | откройте файл `src/game.html` в браузере |



```bash
cp ..env .env
docker compose up --build
```

Остановка с сохранением данных: `docker compose down`. Полный сброс вместе с БД: `docker compose down -v`.


### Переменные окружения

| Переменная | Назначение |
|---|---|
| `DB_URL` | JDBC URL (в Compose собирается автоматически: `jdbc:postgresql://db:5432/${DB_NAME}`) |
| `DB_USER`, `DB_PASSWORD` | Учётные данные PostgreSQL (те же значения получает контейнер `db`) |
| `SECRET_KEY_JWT` | Ключ подписи HS256, минимум 32 символа |
| `DB_NAME`, `DB_HOST_PORT`, `APP_PORT`, `JAVA_OPTS` | Параметры Compose: имя БД, порты на хосте, опции JVM |

Полный список с комментариями — в [`.env`](.env.example).

## Проверка функционала

Базовый URL: `http://localhost:8080`. Все эндпоинты, кроме `/auth/**` и `/token/refresh/**`, требуют заголовок `Authorization: Bearer <accessToken>`. Swagger/OpenAPI в проекте не подключён, ниже перечислены все эндпоинты из контроллеров.

| Метод | Путь | Описание |
|---|---|---|
| POST | `/auth/sign-up` | Регистрация (`201 Success!`, при занятом логине `409`) |
| POST | `/auth/sign-in` | Вход, ответ: `{type, accessToken, refreshToken}` |
| POST | `/token/refresh/access` | Новый access-токен по refresh-токену |
| POST | `/token/refresh/refresh` | Новая пара access + refresh по refresh-токену |
| GET | `/user/get-me` | Профиль текущего пользователя |
| GET | `/user/{userId}` | Логин, роли и партии пользователя |
| POST | `/game/create` | Создать партию: `{"vsAi": true}` или `{"vsAi": false, "player2Login": "bob"}` |
| GET | `/game/my-games` | Все партии текущего игрока |
| GET | `/game/{gameId}` | Состояние партии |
| POST | `/game/{gameId}/accept` | Принять приглашение (второй игрок) |
| POST | `/game/{gameId}/move` | Ход: `{"row": 1..3, "col": 1..3}` |
| GET | `/game/finished` | Завершённые партии игрока |
| GET | `/game/games/not-accepted` | Входящие приглашения, ожидающие принятия |
| GET | `/leaderboard?limit=10` | Таблица лидеров |


## Структура проекта

```text
AP1_Jv_T05B_ID_1421427-1/
├── Dockerfile                      # multi-stage: Gradle 8.14 + JDK 21 → JRE 21 (alpine)
├── docker-compose.yml              # db (PostgreSQL) + app (API)
├── .env.example                    # шаблон переменных окружения
└── src/
    ├── game.html                   # одностраничный клиент (адрес API настраивается в UI)
    └── TicTacToe/
        ├── build.gradle.kts
        └── src/main/
            ├── java/tictactoe/
            │   ├── Application.java
            │   ├── di/             # SpringConfiguration: ручная регистрация бинов, SecurityFilterChain, CORS
            │   ├── web/            # controller (REST), model (DTO), mapper, WebExceptionHandler
            │   ├── domain/         # model (game, security, users), service (auth, game, ai, stat)
            │   └── datasource/     # JPA-сущности, репозитории, мапперы, GameRepositoryService
            └── resources/
                ├── application.properties
                └── db/migration/V1__init_db.sql   # схема: games, turns, users, roles
```

Слои:

- **web** принимает HTTP, валидирует DTO (`@Valid`) и переводит исключения в JSON-ответы (`@RestControllerAdvice`).
- **domain** содержит правила игры (`GameRules`), AI на minimax (`AiService`), JWT-логику (`JwtProvider`, `AuthFilter`) и бизнес-сервисы. Сервисы не зависят от JPA-сущностей.
- **datasource** отвечает за хранение: сущности, Spring Data репозитории и мапперы между сущностями и доменными моделями.
- **di** связывает слои: бины создаются вручную в `@Configuration`, а не через component scan.

## Особенности и ограничения

**Архитектура и данные**

- Схему БД создаёт Flyway (`V1__init_db.sql`), Hibernate только проверяет её (`ddl-auto=validate`). Миграция использует `gen_random_uuid()`, нужен PostgreSQL 13+ (в Compose — 16).
- Игра с компьютером реализована через зарезервированный нулевой UUID `00000000-0000-0000-0000-000000000000`. Внешних ключей на `users` у `player_1_id` / `player_2_id` нет, поэтому такая запись допустима.
- Партия защищена оптимистичной блокировкой (`@Version`, колонка `games.version`).
- AI использует minimax без альфа-бета отсечения (поле 3×3 это допускает). Из равноценных ходов выбирается случайный.
- Graceful shutdown включён (`server.shutdown=graceful`, до 30 секунд на завершение запросов); в Compose задан `stop_grace_period: 40s`.

**Безопасность**

- Аутентификация без сессий: access-токен живёт 15 минут, refresh-токен — 30 дней, подпись HS256 ключом из `SECRET_KEY_JWT` (минимум 32 символа). Refresh-токены stateless: списка отозванных токенов нет, выход с инвалидацией не реализован.
- Невалидный Bearer-токен отклоняется фильтром с `401`; запрос без токена к защищённому пути отклоняется стандартной логикой Spring Security.
- CORS открыт для любых origin (`*` с `allowCredentials`) — удобно для `game.html` с любого адреса, но для продакшена его нужно сузить. CSRF отключён, так как используется Bearer-токен.
- `GET /game/{gameId}` и `GET /user/{userId}` доступны любому аутентифицированному пользователю, проверки участия в партии нет.

**Таблица лидеров**

- Считаются только завершённые партии. Метрика — отношение побед к остальным завершённым партиям игрока (поражения и ничьи); если «остальных» нет, значение равно 1. Из-за этого игроки с серией побед без поражений могут оказаться ниже игрока с отношением больше единицы.

**Сборка и эксплуатация**

- В `T05B` отсутствует `gradle-wrapper.jar`, поэтому Docker-сборка использует образ `gradle:8.14-jdk21`, а локально wrapper нужно сгенерировать (см. выше).
- Spring Boot Actuator не подключён: HTTP-эндпоинта health нет, поэтому у контейнера `app` нет `healthcheck`. Готовность БД контролируется healthcheck-ом `pg_isready`, `app` стартует только после него.
- Автоматических тестов в проекте нет (каталога `src/test` нет), хотя зависимости JUnit подключены.
- `jakarta.validation-api` подключён milestone-версией `4.0.0-M1`.
- Данные PostgreSQL хранятся в именованном томе `pgdata` и переживают `docker compose down`.
- Порт БД проброшен только на `127.0.0.1`, а пароль БД и ключ JWT в шаблоне — dev-значения: перед любым развёртыванием вне локальной машины замените их через `.env`.
