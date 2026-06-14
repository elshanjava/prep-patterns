package concurrent.circuitbreaker.bad;

import java.util.concurrent.atomic.AtomicInteger;

// Вызывает PSP при каждом запросе — даже когда PSP явно недоступен.
// Каждый вызов ждёт timeout (100ms) перед тем как выбросить исключение.
// 100 запросов при упавшем PSP = 10 секунд ожидания вместо мгновенного отказа.
// Нет механизма восстановления: PSP поднялся — мы всё равно продолжаем ждать timeout.
final class BadPspClient {
    private static final long TIMEOUT_MS = 100;
    static final AtomicInteger totalCalls = new AtomicInteger();

    private final AtomicInteger callCount = new AtomicInteger();
    private final int failAfter;

    BadPspClient(int failAfter) { this.failAfter = failAfter; }

    String charge(String paymentId, long amount) throws InterruptedException {
        totalCalls.incrementAndGet();
        Thread.sleep(TIMEOUT_MS); // симуляция сетевого вызова / timeout
        if (callCount.incrementAndGet() > failAfter) {
            throw new RuntimeException("PSP unavailable (timeout after " + TIMEOUT_MS + "ms)");
        }
        return "charged: " + paymentId;
    }
}
