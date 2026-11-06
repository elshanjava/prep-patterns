package tdd;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

public class RateLimiter {

    private final int  maxRequests;
    private final long windowMs;
    private final LongSupplier time;
    private final Map<String, Deque<Long>> map = new HashMap<>();

    public RateLimiter(int maxRequests, long windowMs, LongSupplier time) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
        this.time = time;
    }

    public boolean allow(String userId) {
        long now = time.getAsLong();
        Deque<Long> requestCounter = map.computeIfAbsent(userId, l-> new ArrayDeque<>());

        if (requestCounter.size() < maxRequests) {
            requestCounter.addLast(now);
            return true;
        }
        return false;
    }
}
