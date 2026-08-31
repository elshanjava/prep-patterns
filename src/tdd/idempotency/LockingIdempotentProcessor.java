package tdd.idempotency;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Вариант через per-key lock + double-checked locking (две мапы).
 * Функционально эквивалентен {@link IdempotentProcessor} (Future-мемоизация),
 * но собирает exactly-once вручную из трёх приёмов:
 *
 *   1. быстрый путь: results.get() без блокировки — повторные вызовы дёшевы;
 *   2. per-key lock: отдельный монитор на requestId, разные ключи параллельны;
 *   3. double-checked: под замком перечитываем кэш — пока ждали, другой мог посчитать.
 *
 * Плюс: прозрачно, каждый шаг виден (нагляднее для обучения).
 * Минусы против Future-версии: две мапы, ручной DCL (классический источник багов),
 * мапа locks растёт вечно (нет эвикции — как и results).
 */
public class LockingIdempotentProcessor {

    private final ConcurrentHashMap<String, Object> results = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> locks   = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T process(String requestId, Callable<T> action) throws Exception {
        Object cached = results.get(requestId);
        if (cached != null) return (T) cached;

        // per-key lock: разные requestId не блокируют друг друга
        Object lock = locks.computeIfAbsent(requestId, k -> new Object());
        synchronized (lock) {
            // double-checked: пока мы ждали, другой поток мог уже выполнить
            Object result = results.get(requestId);
            if (result != null) return (T) result;

            T computed = action.call(); // если бросает — не кэшируем
            results.put(requestId, computed);
            return computed;
        }
    }
}
