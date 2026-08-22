package concurrent.zpractice.bulkhead;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BulkheadDemo {
    private static final int  POOL        = 10;   // общий пул сервиса
    private static final int  REQUESTS    = 20;   // запросов к тормозящему PSP
    private static final long PSP_MS      = 200;  // PSP отвечает медленно
    private static final int  MAX_TO_PSP  = 5;    // сколько одновременно пускаем к PSP
    private static final long WAIT_MS     = 50;   // сколько ждём разрешения, потом отказ

    public static void main(String[] args) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(POOL)) {

            var bulkhead = new BulkheadService(MAX_TO_PSP, WAIT_MS);
            var accepted = new AtomicInteger();

            for (int i = 0; i < REQUESTS; i++) {
                executorService.execute(() -> {
                    try {
                        bulkhead.call(()->{
                            try {
                                Thread.sleep(PSP_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            return "charged";
                        });
                        accepted.incrementAndGet();
                    } catch (BulkheadFullException e) {
                        // Отказ — не потеря: вызывающему возвращают 503/Retry-After,
                        // и он решает сам. Главное — поток освободился почти сразу.
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

            }
            executorService.shutdown();
        }
    }
}
