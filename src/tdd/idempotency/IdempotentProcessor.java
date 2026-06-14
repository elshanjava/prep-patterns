package tdd.idempotency;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotentProcessor {

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
