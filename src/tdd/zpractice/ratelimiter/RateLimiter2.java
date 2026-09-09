package tdd.zpractice.ratelimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class RateLimiter2 {
    private final int maxRequests;
    private final long windowMs;
    private final LongSupplier time;
    private final Map<String, Deque<Long>> windows = new ConcurrentHashMap<>();


    public RateLimiter2(int maxRequests, long windowMs, LongSupplier time) {
        if (maxRequests <= 0) throw new IllegalArgumentException("maxRequest should be positive");
        if (windowMs <= 0) throw new IllegalArgumentException("windowMs should be positive");
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
        this.time = time;
    }

    public boolean allow(String userId) {
        long now = time.getAsLong();
        Deque<Long> timestamps = windows.computeIfAbsent(userId, k -> new ArrayDeque<>());
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= windowMs) timestamps.removeFirst();
            if (timestamps.size() < maxRequests) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }
}
