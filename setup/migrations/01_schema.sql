-- ============================================================
--  01_schema.sql — FINTECH SCHEMA
--  users → accounts → transactions / ledger_entries, cards, jobs, payments
--  Запускается автоматически Docker-образом postgres при первом старте.
-- ============================================================

CREATE TABLE users (
    id          BIGINT PRIMARY KEY,
    name        TEXT NOT NULL,
    country     TEXT NOT NULL,
    kyc_status  TEXT NOT NULL DEFAULT 'PENDING',     -- PENDING / VERIFIED / REJECTED
    referred_by BIGINT REFERENCES users(id),         -- реферальное дерево (рекурсия)
    created_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE accounts (
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    currency   TEXT NOT NULL,                        -- EUR / USD / GBP
    balance    NUMERIC(14,2) NOT NULL DEFAULT 0,     -- = сумма SETTLED-транзакций
    status     TEXT NOT NULL DEFAULT 'ACTIVE',       -- ACTIVE / FROZEN / CLOSED
    opened_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE transactions (
    id                   BIGINT PRIMARY KEY,
    account_id           BIGINT NOT NULL REFERENCES accounts(id),
    amount               NUMERIC(14,2) NOT NULL,     -- >0 приход, <0 расход
    type                 TEXT NOT NULL,              -- DEPOSIT / WITHDRAWAL / TRANSFER / FEE
    status               TEXT NOT NULL,              -- SETTLED / PENDING / FAILED
    counterparty_acct_id BIGINT REFERENCES accounts(id),
    created_at           TIMESTAMPTZ NOT NULL
);

CREATE TABLE ledger_entries (                         -- двойная запись
    id             BIGINT PRIMARY KEY,
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    account_id     BIGINT NOT NULL REFERENCES accounts(id),
    direction      TEXT NOT NULL,                    -- DEBIT / CREDIT
    amount         NUMERIC(14,2) NOT NULL,           -- всегда > 0
    created_at     TIMESTAMPTZ NOT NULL
);

CREATE TABLE cards (
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    last4      TEXT NOT NULL,
    network    TEXT NOT NULL,                        -- VISA / MASTERCARD
    status     TEXT NOT NULL,                        -- ACTIVE / BLOCKED
    issued_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE jobs (                                   -- очередь для SKIP LOCKED
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status     TEXT NOT NULL DEFAULT 'PENDING',       -- PENDING / DONE / FAILED
    payload    TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE payments (                               -- для UPSERT-идемпотентности
    idempotency_key TEXT PRIMARY KEY,
    account_id      BIGINT NOT NULL,
    amount          NUMERIC(14,2) NOT NULL,
    status          TEXT NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------- ИНДЕКСЫ ----------------
-- Реалистичные индексы + материал для EXPLAIN / sargability (задача 30).
CREATE INDEX ix_tx_account       ON transactions(account_id);
CREATE INDEX ix_tx_created       ON transactions(created_at);
CREATE INDEX ix_tx_acct_created  ON transactions(account_id, created_at);
CREATE INDEX ix_tx_status        ON transactions(status);
CREATE INDEX ix_acct_user        ON accounts(user_id);
CREATE INDEX ix_led_tx           ON ledger_entries(transaction_id);
CREATE INDEX ix_led_acct         ON ledger_entries(account_id);
CREATE INDEX ix_cards_account    ON cards(account_id);
CREATE INDEX ix_users_referred   ON users(referred_by);
