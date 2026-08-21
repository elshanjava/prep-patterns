package concurrent.threadpool.good;

import java.util.List;
import java.util.concurrent.*;

final class PaymentProcessor implements AutoCloseable {
    // Число потоков ограничено, а вот ОЧЕРЕДЬ — нет: внутри LinkedBlockingQueue без предела.
    // Значит submit() не заблокируется никогда и backpressure отсутствует.
    // Как ограничить очередь — в BoundedPoolDemo (нужен конструктор ThreadPoolExecutor).
    private final ExecutorService pool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    List<Future<String>> processBatch(List<Long> amounts) {
        return amounts.stream()
                .map(amount -> pool.submit(() -> process(amount)))
                .toList();
    }

    private String process(long amountCents) throws InterruptedException {
        Thread.sleep(10);
        return "ok: thread=" + Thread.currentThread().threadId() + " amount=" + amountCents;
    }

    public void close() throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }
    }
}
