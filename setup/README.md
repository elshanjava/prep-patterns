# Fintech SQL Practice DB

Локальная PostgreSQL-база с **реальным объёмом** для тренировки SQL под лайв-кодинг.
Та же fintech-схема, что и в трейнере (`fintech_sql_trainer`), но заполнена сгенерированными данными.

## Что внутри

```
setup/
├── docker-compose.yml
├── migrations/          — монтируется в /docker-entrypoint-initdb.d
│   ├── 01_schema.sql   — таблицы + индексы
│   └── 02_seed.sql     — генерация данных
└── README.md
```

Тренажёр по Java Streams (40 задач с решениями) лежит отдельно:
`src/streams/StreamTasksDemo.java` — самодостаточный, свои данные в `main()`.

Схема: `users → accounts → transactions / ledger_entries`, плюс `cards`, `jobs`, `payments`.

Объём (детерминированный — у всех одинаковый, seed зафиксирован):

| Таблица         | Строк   |
|-----------------|---------|
| users           | 505     |
| accounts        | 1002    |
| transactions    | ~63 000 |
| ledger_entries  | ~16 000 |
| cards           | ~400    |
| jobs            | 200     |
| payments        | 50      |

Данные консистентны:
- `accounts.balance` = сумма её `SETTLED`-транзакций;
- `ledger_entries` сбалансированы по двойной записи (Σ DEBIT = Σ CREDIT);
- `users.referred_by` образует реферальное дерево (глубина до ~7) — для рекурсивных CTE;
- есть намеренные дубли транзакций (для задачи «дедуп») и разрывы в датах активности (для gaps-and-islands);
- 5 краевых юзеров (`Ghost 1-3` без счетов, `Idle 1-2` со счётом, но без транзакций) — под задачу 3
  «юзеры без транзакций» и под ловушку задачи 1 «фильтр в ON vs в WHERE»;
- индексы на `created_at`, `account_id` и т.д. — чтобы `EXPLAIN` показывал реальную разницу Index vs Seq Scan.

## Запуск

```bash
cd setup && docker compose up -d
```

Миграции применяются автоматически при **первом** старте. Через ~10 секунд база готова.

Порт по умолчанию — **5433** (5432 обычно занят локальным Postgres).
Если нужен другой: `PG_PORT=5555 docker compose up -d`.

Проверить, что поднялось:
```bash
docker compose ps
docker compose logs -f db      # видно, как отработали 01_schema и 02_seed
```

## Подключение

**psql (внутри контейнера):**
```bash
docker exec -it fintech-db psql -U fintech -d fintech
```

**psql / DBeaver / DataGrip (с хоста):**

| Параметр  | Значение   |
|-----------|------------|
| Host      | localhost  |
| Port      | 5433       |
| Database  | fintech    |
| User      | fintech    |
| Password  | fintech    |

```bash
psql "postgresql://fintech:fintech@localhost:5433/fintech"
```

## Пересоздать базу с нуля

Миграции в `/docker-entrypoint-initdb.d` выполняются только когда том пустой.
Чтобы перезалить (например, после правки сида):

```bash
docker compose down -v      # -v удаляет том с данными
docker compose up -d        # миграции прогонятся заново
```

## Как тренироваться

1. Держи рядом справочник `fintech_sql_trainer.docx` — там 30 задач по слоям.
2. Примеры в трейнере уже привязаны к этой базе: счёт **1006** (64 транзакции, 57 активных дней),
   таблица `transactions`. Все 30 запросов проверены прогоном — выполняются и возвращают данные.
   Хочешь другой счёт — бери любой реальный:
   ```sql
   SELECT account_id, count(*) FROM transactions GROUP BY 1 ORDER BY 2 DESC LIMIT 1;
   ```
   Единственный намеренно пустой результат — второй запрос задачи 21 (`1 NOT IN (2, NULL)`):
   это и есть демонстрация ловушки, а не ошибка.
3. Тренируй на объёме то, что на маленьком сиде не прочувствовать:
   - `EXPLAIN (ANALYZE, BUFFERS)` — реальные планы, Index vs Seq Scan, стоимость сортировок;
   - keyset-пагинация по 63k строк vs `OFFSET`;
   - оконные функции и `NTILE` на распределении из сотен счетов;
   - gaps-and-islands по настоящим разрывам активности.

## Быстрый smoke-test

```sql
-- топ-5 счетов по обороту
SELECT account_id, SUM(ABS(amount)) AS turnover
FROM transactions
GROUP BY account_id
ORDER BY turnover DESC
LIMIT 5;
```
