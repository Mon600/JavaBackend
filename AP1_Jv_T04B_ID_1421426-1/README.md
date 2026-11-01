# TicTacToe Backend — T04B (Basic-аутентификация, ранняя итерация)

REST-бэкенд онлайн-игры «Крестики-нолики» на Spring Boot: регистрация, вход по HTTP Basic, игра против компьютера (minimax) или против другого зарегистрированного игрока, история партий. В комплекте одностраничный веб-клиент [`src/game.html`](src/game.html). Окружение (API + PostgreSQL) поднимается одной командой через Docker Compose.

> Это предыдущая итерация сервиса. Версия с JWT, Flyway и таблицей лидеров лежит в соседней папке [`../AP1_Jv_T05B_ID_1421427-1`](../AP1_Jv_T05B_ID_1421427-1) и запускается отдельно.

## Стек

| Область | Технология | Версия (из `build.gradle.kts` / конфигов) |
|---|---|---|
| Язык / JDK | Java | 21 (Temurin 21 по настройкам IDE проекта) |
| Сборка | Gradle (wrapper) | 8.14 |
| Фреймворк | Spring Boot (plugin + starters) | 4.1.0 |
| Web | `spring-boot-starter-web` | 4.1.0 |
| Persistence | `spring-boot-starter-data-jpa` (Hibernate, Spring Data JPA) | 4.1.0 |
| Безопасность | `spring-boot-starter-security` (BCrypt, кастомный фильтр с Basic-заголовком) | 4.1.0 |
| Валидация | `jakarta.validation:jakarta.validation-api` | 4.0.0-M1 |
| БД и драйвер | PostgreSQL JDBC | 42.7.12 |
| Boilerplate | Lombok | 1.18.42 |
| Управление зависимостями | `io.spring.dependency-management` | 1.1.7 |
| Тесты (зависимости) | JUnit BOM / Jupiter | 5.10.0 |
| Инфраструктура (Docker) | PostgreSQL | 16-alpine |

Плагины `org.springframework.boot` 4.1.0 и `io.spring.dependency-management` 1.1.7 добавлены в `build.gradle.kts` вместе с Docker-поддержкой: без плагина Boot задачи `bootJar` и `bootRun` недоступны, а обычный jar не содержит зависимостей и не запускается через `java -jar`.

## Быстрый старт (one-command run)

Нужны Docker с Compose v2. Сборка происходит внутри контейнера, локальные JDK и Gradle не требуются.

```bash
cd AP1_Jv_T04B_ID_1421426-1
docker compose up --build
```

После запуска:

| Что | Адрес |
|---|---|
| REST API | http://localhost:8080 |
| PostgreSQL (для psql / IDE) | `localhost:15432`, БД `springdb`, пользователь `postgres` |
| Веб-клиент | откройте файл `src/game.html` в браузере |

Клиент жёстко использует `http://localhost:8080` (константа `API_BASE` в `game.html`), поэтому порт API на хосте лучше не менять.

Значения по умолчанию подходят для локального запуска. Чтобы изменить порты и пароль БД, скопируйте шаблон и отредактируйте его:

```bash
cp .env.example .env
docker compose up --build
```

Остановка с сохранением данных: `docker compose down`. Полный сброс вместе с БД: `docker compose down -v`.

Стеки T04B и T05B используют одинаковые порты по умолчанию (8080 и 15432). Чтобы запустить оба сразу, задайте разные `APP_PORT` и `DB_HOST_PORT` в `.env` (для T04B придётся поправить `API_BASE` в `game.html`). Тома и сети у проектов раздельные.

## Локальная сборка и отладка

Требуется JDK 21. В T04B закоммичен `gradle-wrapper.jar`, поэтому достаточно `./gradlew`.

В `application.properties` жёстко заданы `localhost:15432/springdb`, пользователь `postgres` и пароль `1`. Эти значения переопределяются переменными окружения `SPRING_DATASOURCE_*` без правки кода.

```bash
# 1. Только БД в контейнере (из папки AP1_Jv_T04B_ID_1421426-1)
cp .env.example .env
docker compose up -d db

# 2. Загрузить переменные и перейти в каталог Gradle-проекта
set -a; source .env; set +a
cd src/TicTacToe

# 3. Параметры подключения к БД для приложения
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:${DB_HOST_PORT}/${DB_NAME}"
export SPRING_DATASOURCE_USERNAME="$DB_USER"
export SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"

# Сборка исполняемого jar → build/libs/TicTacToe-1.0-SNAPSHOT.jar
./gradlew bootJar

# Тесты (каталога src/test пока нет, задача завершится без запуска тестов)
./gradlew test

# Запуск из Gradle
./gradlew bootRun

# Запуск jar с параметрами JVM и удалённой отладкой на порту 5005
java -Xms256m -Xmx512m \
  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
  -jar build/libs/TicTacToe-1.0-SNAPSHOT.jar

# Отладка через Gradle (JVM ждёт подключения отладчика на порту 5005)
./gradlew bootRun --debug-jvm
```

Отладка контейнера: в `.env` добавьте агент `jdwp` в `JAVA_OPTS`, а в `docker-compose.yml` раскомментируйте строку с портом `5005`.

```bash
JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### Переменные окружения

| Переменная | Назначение |
|---|---|
| `DB_NAME`, `DB_USER`, `DB_PASSWORD` | БД и учётные данные PostgreSQL; в контейнере `app` превращаются в `SPRING_DATASOURCE_*` |
| `DB_HOST_PORT` | Порт БД на хосте (проброс только на `127.0.0.1`) |
| `APP_PORT` | Порт API на хосте (внутри контейнера всегда 8080) |
| `JAVA_OPTS` | Опции JVM в контейнере |

Полный список с комментариями — в [`.env.example`](.env.example).

## Проверка функционала

Базовый URL: `http://localhost:8080`. Эндпоинты `/auth/**` открыты. Остальные требуют заголовок `Authorization: Basic base64(login:password)` в **каждом** запросе (фильтр проверяет пароль через BCrypt при каждом обращении). Swagger/OpenAPI в проекте не подключён, ниже перечислены все эндпоинты из контроллеров.

| Метод | Путь | Описание |
|---|---|---|
| POST | `/auth/signup` | Регистрация: `{"login","password"}` (`201 Success!`, при занятом логине `409`) |
| POST | `/auth/login` | Проверка Basic-заголовка, ответ — UUID пользователя (`201`) |
| GET | `/user/{userId}` | Логин и партии пользователя |
| POST | `/game/create` | Создать партию: `{"vsAi": true}` или `{"vsAi": false, "player2Login": "bob"}` |
| GET | `/game/my-games` | Все партии текущего игрока |
| GET | `/game/{gameId}` | Состояние партии |
| POST | `/game/{gameId}/accept` | Принять приглашение (второй игрок) |
| POST | `/game/{gameId}/move` | Ход: `{"row": 1..3, "col": 1..3}` |
| GET | `/game/all` | Все партии в системе |
| GET | `/game/games/not-accepted` | Входящие приглашения, ожидающие принятия |

Сценарий «регистрация → игра с компьютером» (нужен `jq`; `curl -u` сам формирует Basic-заголовок):

```bash
BASE=http://localhost:8080

# Регистрация (201 Success!)
curl -i -X POST "$BASE/auth/signup" \
  -H 'Content-Type: application/json' \
  -d '{"login":"alice","password":"password123"}'

# Вход: вернёт UUID пользователя
curl -i -u alice:password123 -X POST "$BASE/auth/login"

# Новая партия против компьютера
GAME_ID=$(curl -s -u alice:password123 -X POST "$BASE/game/create" \
  -H 'Content-Type: application/json' \
  -d '{"vsAi":true}' | jq -r .id)

# Ход в центр поля (строки и столбцы нумеруются с 1)
curl -s -u alice:password123 -X POST "$BASE/game/$GAME_ID/move" \
  -H 'Content-Type: application/json' \
  -d '{"row":2,"col":2}' | jq

# Мои партии
curl -s -u alice:password123 "$BASE/game/my-games" | jq
```

Игра двух людей: первый игрок создаёт партию `{"vsAi":false,"player2Login":"bob"}`, второй видит её в `/game/games/not-accepted`, принимает через `POST /game/{gameId}/accept`, после чего игроки ходят по очереди через `/game/{gameId}/move`.

Состояния партии (`GameState`): `WAITING`, `PLAYER_TURN`, `PLAYER_WIN`, `AI_WIN`, `DRAW`.

## Структура проекта

```text
AP1_Jv_T04B_ID_1421426-1/
├── Dockerfile                      # multi-stage: Gradle 8.14 + JDK 21 → JRE 21 (alpine)
├── docker-compose.yml              # db (PostgreSQL) + app (API)
├── .env.example                    # шаблон переменных окружения
└── src/
    ├── game.html                   # одностраничный клиент (API_BASE = http://localhost:8080)
    └── TicTacToe/
        ├── build.gradle.kts
        └── src/main/
            ├── java/tictactoe/
            │   ├── Application.java
            │   ├── di/             # SpringConfiguration: ручная регистрация бинов и SecurityFilterChain
            │   ├── web/            # controller (REST), model (DTO), mapper, WebExceptionHandler
            │   ├── domain/         # model (игра, пользователь, AuthFilter), service (auth, game, ai)
            │   └── datasource/     # JPA-сущности, репозитории, мапперы, GameRepositoryService
            └── resources/
                └── application.properties
```

Слои:

- **web** принимает HTTP, валидирует DTO (`@Valid`) и переводит исключения в JSON-ответы (`@RestControllerAdvice`).
- **domain** содержит правила игры, AI на minimax (`AiService`), аутентификацию (`AuthFilter`, `AuthService`) и бизнес-сервисы.
- **datasource** отвечает за хранение: сущности, Spring Data репозитории и мапперы между сущностями и доменными моделями.
- **di** связывает слои: бины создаются вручную в `@Configuration`, а не через component scan.

## Особенности и ограничения

**Архитектура и данные**

- Схема БД создаётся Hibernate (`spring.jpa.hibernate.ddl-auto=update`), миграций нет. Для продакшена это стоит заменить на Flyway, как сделано в T05B.
- Конфигурация подключения к БД в `application.properties` жёстко привязана к `localhost:15432/springdb`. В Docker она переопределяется через `SPRING_DATASOURCE_*`.
- Игра с компьютером реализована через зарезервированный нулевой UUID в роли второго игрока.
- AI использует minimax без альфа-бета отсечения (поле 3×3 это допускает).

**Безопасность**

- Аутентификация: HTTP Basic в каждом запросе, пароль проверяется через BCrypt при каждом вызове. Сессий и токенов нет. Передача по HTTP без TLS небезопасна, поэтому для публичного развёртывания нужен HTTPS.
- Разбор заголовка делит строку по `:` и требует ровно две части, поэтому пароль с двоеточием не пройдёт. Валидация регистрации пробелы запрещает, а двоеточие — нет.
- Клиент `game.html` хранит логин и пароль в состоянии страницы и отправляет их с каждым запросом.
- CORS разрешён для любых origin через `@CrossOrigin(origins = "*")` на контроллерах. CSRF отключён.
- `GET /game/all` отдаёт все партии любому аутентифицированному пользователю, а `GET /game/{gameId}` и `GET /user/{userId}` не проверяют участие в партии.

**Сборка и эксплуатация**

- В `build.gradle.kts` добавлены плагины Spring Boot и dependency-management (см. раздел «Стек»), чтобы собирать исполняемый jar.
- Spring Boot Actuator не подключён: HTTP-эндпоинта health нет, поэтому у контейнера `app` нет `healthcheck`. Готовность БД контролируется healthcheck-ом `pg_isready`, `app` стартует только после него.
- Автоматических тестов в проекте нет (каталога `src/test` нет), хотя зависимости JUnit подключены.
- `jakarta.validation-api` подключён milestone-версией `4.0.0-M1`.
- Данные PostgreSQL хранятся в именованном томе `pgdata` и переживают `docker compose down`.
- В репозитории закоммичены файлы IDE (`.idea`) и `code.txt`. Они исключены из Docker-контекста через `.dockerignore`.
- Порт БД проброшен только на `127.0.0.1`, а пароль БД в шаблоне — dev-значение: перед любым развёртыванием вне локальной машины замените его через `.env`.
