package tdd.ratelimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private final int  maxRequests;
    private final long windowMs;
    private final ConcurrentHashMap<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs    = windowMs;
    }

    public boolean allow(String userId) {
        long now = System.currentTimeMillis();
        // computeIfAbsent + per-user synchronized: разные users не блокируют друг друга
        Deque<Long> timestamps = windows.computeIfAbsent(userId, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            // удаляем метки вышедшие за пределы скользящего окна
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= windowMs) {
                timestamps.pollFirst();
            }
            if (timestamps.size() < maxRequests) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }
}
