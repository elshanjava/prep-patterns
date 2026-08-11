-- ============================================================
--  02_seed.sql — генерация реалистичного объёма данных
--  Детерминированно (setseed) → база одинаковая при каждом старте.
--  ~500 users · ~1000 accounts · ~63k transactions · ~16k ledger · cards/jobs/payments
--  Консистентно: accounts.balance = сумма SETTLED-транзакций; ledger сбалансирован.
-- ============================================================

SELECT setseed(0.4242);

-- ---------- USERS (500) ----------
INSERT INTO users (id, name, country, kyc_status, referred_by, created_at)
SELECT g,
       (ARRAY['Anna','Boris','Cara','Dmitry','Elena','Farid','Gita','Hasan','Irina','Javid',
              'Kamran','Lala','Murad','Nina','Omar','Polina','Rustam','Sara','Timur','Vera'])[1+floor(random()*20)::int]
         || ' ' ||
       (ARRAY['Ivanov','Klein','Meyer','Nowak','Silva','Costa','Yilmaz','Kaya','Novak','Horvath',
              'Aliyev','Mammadov','Weber','Fischer','Santos','Dubois','Kowalski','Popov','Smit','Jansen'])[1+floor(random()*20)::int],
       (ARRAY['DE','NL','FR','ES','PT','GB','PL','AZ'])[1+floor(random()*8)::int],
       (ARRAY['VERIFIED','VERIFIED','VERIFIED','VERIFIED','PENDING','PENDING','REJECTED'])[1+floor(random()*7)::int],
       CASE WHEN g > 1 AND random() < 0.6 THEN (1+floor(random()*(g-1)))::bigint ELSE NULL END,
       TIMESTAMPTZ '2024-01-01' + (random()*400) * interval '1 day'
FROM generate_series(1, 500) g;

-- ---------- ACCOUNTS (1-2 на юзера, id с 1001) ----------
INSERT INTO accounts (id, user_id, currency, balance, status, opened_at)
SELECT 1000 + row_number() OVER (ORDER BY u.id, s),
       u.id,
       (ARRAY['EUR','EUR','EUR','EUR','USD','USD','GBP'])[1+floor(random()*7)::int],
       0,
       (ARRAY['ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE','FROZEN','CLOSED'])[1+floor(random()*7)::int],
       u.created_at + (random()*20) * interval '1 day'
FROM users u
CROSS JOIN LATERAL generate_series(1, 1 + (floor(random()*2))::int) s;

-- ---------- TRANSACTIONS (30-120 на счёт, ~55k) ----------
INSERT INTO transactions (id, account_id, amount, type, status, counterparty_acct_id, created_at)
SELECT row_number() OVER (ORDER BY base.account_id, base.created_at, base.rn),
       base.account_id,
       CASE base.type_k
         WHEN 1 THEN  round((random()*4950 + 50)::numeric, 2)      -- DEPOSIT
         WHEN 5 THEN -round((random()*19   + 1)::numeric, 2)       -- FEE
         ELSE        -round((random()*1990 + 10)::numeric, 2)      -- WITHDRAWAL / TRANSFER
       END,
       (ARRAY['DEPOSIT','WITHDRAWAL','WITHDRAWAL','WITHDRAWAL','FEE','TRANSFER'])[base.type_k],
       (ARRAY['SETTLED','SETTLED','SETTLED','SETTLED','SETTLED','SETTLED','SETTLED',
              'PENDING','FAILED'])[1+floor(random()*9)::int],
       NULL,
       base.created_at
FROM (
  -- type_k считается в target-list реального источника строк -> random() per-row
  SELECT a.id AS account_id,
         gs   AS rn,
         1 + floor(random()*6)::int AS type_k,
         a.opened_at + (random()*300) * interval '1 day'
                     + (random()*86400) * interval '1 second' AS created_at
  FROM accounts a
  CROSS JOIN LATERAL generate_series(1, 30 + (floor(random()*90))::int) gs
) base;

-- counterparty для TRANSFER (детерминированный псевдо-рандом на существующий счёт, не сам себя)
WITH bounds AS (SELECT min(id) lo, count(*) n FROM accounts)
UPDATE transactions t
SET counterparty_acct_id = b.lo + ((t.id * 97 + 13) % b.n)
FROM bounds b
WHERE t.type = 'TRANSFER'
  AND b.lo + ((t.id * 97 + 13) % b.n) <> t.account_id;

-- ---------- LEDGER (двойная запись для SETTLED-переводов) ----------
WITH tr AS (
  SELECT id, account_id, counterparty_acct_id, amount, created_at
  FROM transactions
  WHERE type='TRANSFER' AND status='SETTLED' AND counterparty_acct_id IS NOT NULL
),
entries AS (
  SELECT id AS transaction_id, account_id,           'DEBIT'  AS direction, ABS(amount) AS amount, created_at FROM tr
  UNION ALL
  SELECT id AS transaction_id, counterparty_acct_id, 'CREDIT' AS direction, ABS(amount) AS amount, created_at FROM tr
)
INSERT INTO ledger_entries (id, transaction_id, account_id, direction, amount, created_at)
SELECT row_number() OVER (ORDER BY transaction_id, direction),
       transaction_id, account_id, direction, amount, created_at
FROM entries;

-- ---------- дубликаты транзакций (для задачи 14 «дедуп») ----------
INSERT INTO transactions (id, account_id, amount, type, status, counterparty_acct_id, created_at)
SELECT (SELECT max(id) FROM transactions) + row_number() OVER (ORDER BY id),
       account_id, amount, type, status, counterparty_acct_id, created_at
FROM (SELECT * FROM transactions WHERE status='SETTLED' ORDER BY random() LIMIT 30) d;

-- ---------- баланс = сумма SETTLED ----------
UPDATE accounts a
SET balance = COALESCE((SELECT SUM(t.amount) FROM transactions t
                        WHERE t.account_id = a.id AND t.status='SETTLED'), 0);

-- ---------- CARDS (~40% счетов) ----------
INSERT INTO cards (id, user_id, account_id, last4, network, status, issued_at)
SELECT 5000 + row_number() OVER (ORDER BY a.id),
       a.user_id, a.id,
       lpad((floor(random()*10000))::int::text, 4, '0'),
       (ARRAY['VISA','VISA','MASTERCARD'])[1+floor(random()*3)::int],
       (ARRAY['ACTIVE','ACTIVE','ACTIVE','BLOCKED'])[1+floor(random()*4)::int],
       a.opened_at + (random()*30) * interval '1 day'
FROM accounts a
WHERE random() < 0.4;

-- ---------- JOBS (очередь ~200) ----------
INSERT INTO jobs (status, payload, created_at)
SELECT (ARRAY['PENDING','PENDING','PENDING','DONE'])[1+floor(random()*4)::int],
       'settle:tx:' || (1000 + floor(random()*50000))::int,
       TIMESTAMPTZ '2025-01-01' + (random()*200) * interval '1 day'
FROM generate_series(1, 200);

-- ---------- PAYMENTS (idempotency, ~50) ----------
INSERT INTO payments (idempotency_key, account_id, amount, status, updated_at)
SELECT 'idem-' || g,
       1000 + (1 + floor(random()*(SELECT count(*) FROM accounts)))::int,
       round((random()*1000 + 10)::numeric, 2),
       (ARRAY['SETTLED','PENDING'])[1+floor(random()*2)::int],
       now()
FROM generate_series(1, 50) g;

-- ---------- КРАЕВЫЕ СЛУЧАИ (под задачи 1 и 3) ----------
-- Без них задача 3 «юзеры без единой транзакции» возвращает пусто (у всех
-- сгенерированных юзеров транзакции есть), а ловушка задачи 1 «фильтр в ON
-- vs в WHERE» не воспроизводится: оба варианта дают одинаковый результат.

-- 3 юзера вообще без счетов
INSERT INTO users (id, name, country, kyc_status, referred_by, created_at)
SELECT 500 + g, 'Ghost ' || g, 'DE', 'PENDING', NULL,
       TIMESTAMPTZ '2025-06-01' + g * interval '1 day'
FROM generate_series(1, 3) g;

-- 2 юзера со счётом, но без единой транзакции -> исчезают при фильтре в WHERE
INSERT INTO users (id, name, country, kyc_status, referred_by, created_at)
SELECT 503 + g, 'Idle ' || g, 'NL', 'VERIFIED', NULL,
       TIMESTAMPTZ '2025-06-10' + g * interval '1 day'
FROM generate_series(1, 2) g;

INSERT INTO accounts (id, user_id, currency, balance, status, opened_at)
SELECT 2000 + g, 503 + g, 'EUR', 0, 'ACTIVE',      -- balance=0 согласован: SETTLED-транзакций нет
       TIMESTAMPTZ '2025-06-15' + g * interval '1 day'
FROM generate_series(1, 2) g;

ANALYZE;
