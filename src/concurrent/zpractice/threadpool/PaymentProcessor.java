package concurrent.zpractice.threadpool;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PaymentProcessor implements AutoCloseable {
    private final ExecutorService pool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    private String process(long amountCents) throws InterruptedException {
        Thread.sleep(10);
        return "ok: thread=" + Thread.currentThread().threadId() + " amount=" + amountCents;
    }

    public List<Future<String>> processBatch(List<Long> amounts) {
        return amounts.stream()
                .map(amount -> pool.submit(()->process(amount)))
                .toList();
    }

    @Override
    public void close() throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)){
            pool.shutdown();
        }
    }

}
