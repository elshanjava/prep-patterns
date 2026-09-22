# SQL — эталонные решения

Открывать после своей попытки. Источник — планировщик `setup/prep_planner (2).html`.


## БАЗА

### 1. Баланс пользователя (SETTLED)

Сумма проведённых транзакций по всем счетам юзера.

*Фильтр по status — в ON, а не в WHERE: иначе LEFT JOIN схлопнется в INNER и юзеры без SETTLED исчезнут.*

```sql
SELECT u.id, u.name,
       COALESCE(SUM(t.amount),0) AS settled_balance
FROM users u
JOIN accounts a ON a.user_id = u.id
LEFT JOIN transactions t
       ON t.account_id = a.id AND t.status='SETTLED'
GROUP BY u.id, u.name;
```

### 2. Активные счета по валюте

Валюты, где больше одного активного счёта.

*WHERE фильтрует строки ДО группировки, HAVING — агрегаты ПОСЛЕ. Путать нельзя.*

```sql
SELECT currency, COUNT(*) AS n, SUM(balance) AS total
FROM accounts
WHERE status='ACTIVE'
GROUP BY currency
HAVING COUNT(*) > 1;
```

### 3. Юзеры без единой транзакции

Ни одной транзакции ни по одному счёту.

*LEFT JOIN…IS NULL на уровне юзера ломается при нескольких счетах (уедет тот, у кого хоть один счёт пуст). Нужен коррелированный NOT EXISTS.*

```sql
SELECT u.id, u.name
FROM users u
WHERE NOT EXISTS (
  SELECT 1 FROM accounts a
  JOIN transactions t ON t.account_id = a.id
  WHERE a.user_id = u.id);
```


## ОКНА

### 4. ROW_NUMBER: нумерация по счёту

Пронумеровать транзакции внутри счёта по времени.

*Без tie-breaker (добавь id в ORDER BY) нумерация недетерминирована при равных created_at.*

```sql
SELECT account_id, id, amount,
       ROW_NUMBER() OVER (
         PARTITION BY account_id
         ORDER BY created_at, id) AS seq
FROM transactions;
```

### 5. RANK vs DENSE_RANK

Ранг счетов по балансу.

*RANK оставляет дыры после ничьих (1,1,3), DENSE_RANK — нет (1,1,2), ROW_NUMBER всегда уникален.*

```sql
SELECT id, balance,
  RANK()       OVER (ORDER BY balance DESC) AS rnk,
  DENSE_RANK() OVER (ORDER BY balance DESC) AS dense_rnk
FROM accounts;
```

### 6. LAG: дельта с предыдущей

Разница суммы с предыдущей транзакцией счёта.

*У первой строки LAG = NULL → разница NULL. Нужен дефолт — LAG(amount,1,0).*

```sql
SELECT id, amount,
  amount - LAG(amount) OVER (
    PARTITION BY account_id
    ORDER BY created_at, id) AS diff_prev
FROM transactions WHERE account_id=1006;
```

### 7. LEAD: время до следующей

Интервал до следующей транзакции.

*Последняя строка партиции → LEAD NULL. Разница двух timestamptz даёт interval.*

```sql
SELECT id, created_at,
  LEAD(created_at) OVER (
    PARTITION BY account_id
    ORDER BY created_at, id) - created_at AS gap
FROM transactions WHERE account_id=1006;
```

### 8. Running balance

Нарастающий баланс по счёту.

*SQL-аналог задачи, где в Java стрим был НЕ нужен (№34). Явный фрейм ROWS важен: RANGE при равных ключах суммирует все ничьи разом.*

```sql
SELECT id, amount,
  SUM(amount) OVER (
    PARTITION BY account_id
    ORDER BY created_at, id
    ROWS BETWEEN UNBOUNDED PRECEDING
             AND CURRENT ROW) AS running_balance
FROM transactions WHERE account_id=1006;
```

### 9. Percent of total

Доля транзакции в обороте счёта.

*Оконный SUM без ORDER BY = сумма по всей партиции (то, что нужно). 100.0, чтобы не поймать целочисленное деление.*

```sql
SELECT id, amount,
  ROUND(100.0*ABS(amount)
    / SUM(ABS(amount)) OVER (
        PARTITION BY account_id),1) AS pct
FROM transactions WHERE account_id=1006;
```

### 10. NTILE: квартили

Разбить юзеров на 4 корзины по обороту.

*NTILE делит максимально ровно; при неделящемся числе строк ранние корзины крупнее на 1.*

```sql
WITH turnover AS (
  SELECT a.user_id, SUM(ABS(t.amount)) AS vol
  FROM transactions t JOIN accounts a ON a.id=t.account_id
  GROUP BY a.user_id)
SELECT user_id, vol,
  NTILE(4) OVER (ORDER BY vol DESC) AS quartile
FROM turnover;
```

### 11. Moving average (ROWS vs RANGE)

Скользящее среднее по 3 транзакциям.

*ROWS = физические строки, RANGE = логический диапазон значений ORDER BY — на дубликатах ключа дают РАЗНОЕ. Дефолтный фрейм при ORDER BY без явного — RANGE, частый сюрприз.*

```sql
SELECT id, amount,
  ROUND(AVG(amount) OVER (
    PARTITION BY account_id
    ORDER BY created_at, id
    ROWS BETWEEN 2 PRECEDING
             AND CURRENT ROW),2) AS mov_avg3
FROM transactions WHERE account_id=1006;
```

### 12. Top-3 транзакции по счёту

Три крупнейшие (по модулю) на каждый счёт.

*Оконную функцию нельзя фильтровать в WHERE (она считается после WHERE) — нужен подзапрос/CTE. RANK вместо ROW_NUMBER вернёт >3 при ничьих.*

```sql
WITH ranked AS (
  SELECT account_id, id, amount,
    ROW_NUMBER() OVER (
      PARTITION BY account_id
      ORDER BY ABS(amount) DESC, id) AS rn
  FROM transactions)
SELECT account_id, id, amount
FROM ranked WHERE rn <= 3;
```

### 13. Последняя запись по счёту (DISTINCT ON)

Самая свежая транзакция каждого счёта.

*Postgres-специфика. ORDER BY ОБЯЗАН начинаться с DISTINCT ON-колонок, иначе «первая» строка недетерминирована. Аналог Java toMap last-wins (№10).*

```sql
SELECT DISTINCT ON (account_id)
       account_id, id, amount, created_at
FROM transactions
ORDER BY account_id, created_at DESC, id DESC;
```


## CTE, ВРЕМЯ, РЕКУРСИЯ

### 14. Дедуп полных дублей

Найти дубли по бизнес-ключу.

*rn=1 оставляем, rn>1 — дубли. Для удаления: DELETE … USING этого CTE по id.*

```sql
WITH d AS (
  SELECT id, ROW_NUMBER() OVER (
    PARTITION BY account_id, amount, type,
                 status, created_at
    ORDER BY id) AS rn
  FROM transactions)
SELECT id FROM d WHERE rn > 1;
```

### 15. Gaps: дни без активности

Календарные дни без транзакций по счёту.

*generate_series строит полный календарь, дальше anti-join. Без календаря «пропущенные» дни в данных не увидеть.*

```sql
WITH bounds AS (
  SELECT MIN(created_at::date) lo, MAX(created_at::date) hi
  FROM transactions WHERE account_id=1006),
days AS (SELECT gs::date d FROM bounds,
  generate_series(lo,hi,interval '1 day') gs)
SELECT d FROM days
WHERE NOT EXISTS (SELECT 1 FROM transactions t
  WHERE t.account_id=1006
    AND t.created_at::date = days.d);
```

### 16. Islands: непрерывные периоды

Отрезки последовательных активных дней.

*Классика: дата − ROW_NUMBER даёт константу внутри непрерывного отрезка → группируем по ней. Мало кто помнит идиому под таймером.*

```sql
WITH d AS (SELECT DISTINCT created_at::date AS day
  FROM transactions WHERE account_id=1006),
grp AS (SELECT day,
  day - (ROW_NUMBER() OVER (ORDER BY day))::int AS island
  FROM d)
SELECT MIN(day) start_day, MAX(day) end_day,
       COUNT(*) days
FROM grp GROUP BY island;
```

### 17. CTE для читаемости

Многошаговая агрегация без вложенных подзапросов.

*В PG12+ короткие CTE инлайнятся автоматически — не бойся их как «барьера материализации». Управляй явно: MATERIALIZED / NOT MATERIALIZED.*

```sql
WITH per_type AS (
  SELECT type, COUNT(*) n, SUM(ABS(amount)) vol,
         AVG(ABS(amount)) avg_amt
  FROM transactions WHERE status='SETTLED'
  GROUP BY type)
SELECT type, n, vol, ROUND(avg_amt,2) AS avg_amt
FROM per_type WHERE vol > 500;
```

### 18. Рекурсивный CTE: ряд дат

Дневной оборот за период БЕЗ пропусков дат.

*Генерируешь календарь и LEFT JOIN данные — иначе «пустые» дни выпадут из отчёта. В PG проще generate_series, но рекурсию просят показать явно.*

```sql
WITH RECURSIVE cal(d) AS (
  SELECT DATE '2025-04-01'
  UNION ALL
  SELECT d+1 FROM cal WHERE d < DATE '2025-04-06')
SELECT cal.d, COALESCE(SUM(ABS(t.amount)),0) AS vol
FROM cal LEFT JOIN transactions t
  ON t.account_id=1006 AND t.created_at::date=cal.d
GROUP BY cal.d;
```

### 19. Рекурсивный CTE: иерархия

Реферальное дерево (кто кого привёл) + глубина.

*anchor (корни) + рекурсивная часть через UNION ALL. На данных с циклами нужна защита (UNION вместо UNION ALL или лимит глубины), иначе бесконечность.*

```sql
WITH RECURSIVE ref_tree AS (
  SELECT id, name, referred_by, 1 AS depth,
         name::text AS path
  FROM users WHERE referred_by IS NULL
  UNION ALL
  SELECT u.id, u.name, u.referred_by,
         rt.depth+1, rt.path||' > '||u.name
  FROM users u JOIN ref_tree rt
    ON u.referred_by = rt.id)
SELECT id, name, depth, path FROM ref_tree;
```


## ОТЧЁТЫ И МНОЖЕСТВА

### 20. Пивот через FILTER

Обороты по типам транзакций в колонки.

*FILTER (WHERE …) чище, чем SUM(CASE WHEN … END). Пустая ячейка = NULL — оборачивай в COALESCE(…,0) при необходимости.*

```sql
SELECT account_id,
  SUM(ABS(amount)) FILTER (WHERE type='DEPOSIT')    AS deposits,
  SUM(ABS(amount)) FILTER (WHERE type='WITHDRAWAL') AS withdrawals,
  SUM(ABS(amount)) FILTER (WHERE type='FEE')        AS fees,
  SUM(ABS(amount)) FILTER (WHERE type='TRANSFER')   AS transfers
FROM transactions WHERE status='SETTLED'
GROUP BY account_id;
```

### 21. Счета без карт + ловушка NOT IN

Счета, к которым не привязана карта.

*Один NULL в подзапросе NOT IN — и весь результат пустой. NOT EXISTS от этого не страдает. Любимый вопрос на внимательность.*

```sql
SELECT a.id FROM accounts a
WHERE NOT EXISTS (SELECT 1 FROM cards c
                  WHERE c.account_id = a.id);

-- ❌ ловушка: вернёт 0 строк вместо ожидаемой
SELECT 1 WHERE 1 NOT IN (2, NULL);
```

### 22. EXISTS: полу-джойн

Юзеры, у кого есть хоть одна FAILED транзакция.

*EXISTS не размножает строки (в отличие от JOIN) → не нужен DISTINCT. Short-circuit: остановится на первом совпадении.*

```sql
SELECT u.id, u.name FROM users u
WHERE EXISTS (
  SELECT 1 FROM accounts a
  JOIN transactions t ON t.account_id=a.id
  WHERE a.user_id=u.id AND t.status='FAILED');
```

### 23. EXCEPT: разность множеств

Счета с транзакциями, но без карт.

*EXCEPT убирает дубли (как UNION). Нужны дубли — EXCEPT ALL. Порядок операндов важен: A−B ≠ B−A.*

```sql
SELECT account_id FROM transactions
EXCEPT
SELECT account_id FROM cards;
```

### 24. Условная агрегация FILTER

Разбивка по статусам одним запросом.

*Один проход вместо 3 подзапросов. Прямой аналог Java teeing (№14) — «несколько агрегатов за один проход».*

```sql
SELECT account_id, COUNT(*) total,
  COUNT(*) FILTER (WHERE status='SETTLED') settled,
  COUNT(*) FILTER (WHERE status='PENDING') pending,
  COUNT(*) FILTER (WHERE status='FAILED')  failed
FROM transactions GROUP BY account_id;
```


## КОНКУРЕНТНОСТЬ

### 25. UPSERT: идемпотентный платёж

Повторный вызов не создаёт вторую запись.

*Прямая fintech-тема: идемпотентность через unique-ключ. EXCLUDED — то, что пытались вставить. Не нужен апдейт — DO NOTHING.*

```sql
INSERT INTO payments
  (idempotency_key, account_id, amount, status)
VALUES ('idem-abc-123', 1006, 250.00, 'SETTLED')
ON CONFLICT (idempotency_key)
DO UPDATE SET status = EXCLUDED.status,
              updated_at = now()
RETURNING idempotency_key, status;
```

### 26. Воркер-очередь: SKIP LOCKED

N инстансов разбирают задания без гонок.

*Без SKIP LOCKED воркеры встают в очередь на одну строку. Работает только внутри транзакции. Классический платёжный паттерн (settle-очередь).*

```sql
BEGIN;
SELECT id, payload FROM jobs
WHERE status='PENDING'
ORDER BY created_at
FOR UPDATE SKIP LOCKED
LIMIT 2;
-- обработать, UPDATE status='DONE'
COMMIT;
```

### 27. Advisory lock по ключу

Сериализовать критическую секцию по произвольному ключу.

*xact-версия снимается на COMMIT/ROLLBACK сама; сессионная pg_advisory_lock требует ручного unlock — легко словить утечку блокировки.*

```sql
BEGIN;
SELECT pg_advisory_xact_lock(
         hashtext('account:1006'));
-- критическая секция под блокировкой по ключу
COMMIT;  -- xact-lock снимается автоматически
```

### 28. Списание под гонкой (write skew)

Защита от двойного списания.

*Два конкурентных «прочитал баланс → списал» под READ COMMITTED дают write skew. Лечим: FOR UPDATE (пессимизм, блок строки) ИЛИ SERIALIZABLE/SSI (оптимизм, один tx откатится). Условие balance>=100 — вторая линия.*

```sql
BEGIN;
SELECT balance FROM accounts
WHERE id=1006 FOR UPDATE;
UPDATE accounts SET balance = balance - 100
WHERE id=1006 AND balance >= 100;
COMMIT;
```


## ПРОД

### 29. Keyset-пагинация

Стабильная страница без OFFSET.

*Сравнение кортежей (created_at,id) > (…) — курсор не съедет при вставках, в отличие от OFFSET (у стрима это был обход всего, Java №5). Нужен составной индекс (created_at,id).*

```sql
SELECT id, account_id, created_at
FROM transactions
WHERE (created_at, id)
    > (TIMESTAMPTZ '2025-04-03 15:10', 1004)
ORDER BY created_at, id
LIMIT 5;
```

### 30. EXPLAIN: sargability

Почему индекс не используется.

*Функция/каст на индексируемой колонке убивает индекс (не sargable) — переписывай в диапазон. Проверено на 200k строк: слева Parallel Seq Scan, справа Bitmap Index Scan. Та же беда — несовпадение типов колонки и литерала.*

```sql
-- ❌ функция на колонке -> Seq Scan
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM transactions
WHERE date(created_at) = DATE '2025-03-01';

-- ✅ sargable диапазон -> Index Scan
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM transactions
WHERE created_at >= '2025-03-01'
  AND created_at <  '2025-03-02';
```
