-- 4. Пронумеровать транзакции внутри каждого счёта по времени (от старых к новым).
--    Вернуть: account_id, created_at, amount, rn — только для account_id = 1.
--    Первая оконная функция: ROW_NUMBER() OVER (PARTITION BY ... ORDER BY ...).

