# JavaBackend

Две итерации учебного бэкенда «Крестики-нолики» на Spring Boot 4.1 / Java 21 / PostgreSQL. Каждый проект самодостаточен: у него свои `Dockerfile`, `docker-compose.yml`, `.env.example` и README.

| Проект | Что внутри | Запуск |
|---|---|---|
| [`AP1_Jv_T05B_ID_1421427-1`](AP1_Jv_T05B_ID_1421427-1) | Актуальная версия: JWT (access/refresh), Flyway-миграции, таблица лидеров | `cd AP1_Jv_T05B_ID_1421427-1 && docker compose up --build` |
| [`AP1_Jv_T04B_ID_1421426-1`](AP1_Jv_T04B_ID_1421426-1) | Предыдущая итерация: HTTP Basic, схема через `ddl-auto=update` | `cd AP1_Jv_T04B_ID_1421426-1 && docker compose up --build` |

Оба проекта по умолчанию используют порты 8080 (API) и 15432 (PostgreSQL), поэтому одновременно их можно запустить только с разными `APP_PORT` и `DB_HOST_PORT` в `.env`. Подробности по стеку, эндпоинтам, отладке и ограничениям — в README каждого проекта.
