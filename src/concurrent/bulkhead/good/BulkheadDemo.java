package concurrent.bulkhead.good;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Тот же сценарий, что в bad, но вызовы к PSP проходят через переборку.
// Лишние отваливаются БЫСТРО и освобождают потоки — остальному сервису есть чем дышать.
public class BulkheadDemo {

    private static final int  POOL        = 10;   // общий пул сервиса
    private static final int  REQUESTS    = 20;   // запросов к тормозящему PSP
    private static final long PSP_MS      = 200;
    private static final int  MAX_TO_PSP  = 5;    // сколько одновременно пускаем к PSP
    private static final long WAIT_MS     = 50;   // сколько ждём разрешения, потом отказ

    public static void main(String[] args) throws Exception {
        System.out.println("== Bulkhead [GOOD] — семафор ограничивает параллелизм ==");

        ExecutorService pool = Executors.newFixedThreadPool(POOL);
        var bulkhead = new Bulkhead(MAX_TO_PSP, WAIT_MS);
        var accepted = new AtomicInteger();

        for (int i = 0; i < REQUESTS; i++) {
            pool.execute(() -> {
                try {
                    bulkhead.call(() -> {
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

        long submittedAt = System.currentTimeMillis();
        var  startedAt   = new CompletableFuture<Long>();
        pool.execute(() -> startedAt.complete(System.currentTimeMillis()));

        long waited = startedAt.get() - submittedAt;

        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println();
        System.out.printf("одновременных вызовов к PSP: %d (лимит %d — соблюдён)%n",
                bulkhead.maxObservedConcurrency(), MAX_TO_PSP);
        System.out.printf("пропущено: %d, отклонено быстро: %d из %d%n",
                accepted.get(), bulkhead.rejectedCount(), REQUESTS);
        System.out.printf("быстрый запрос 'покажи баланс' ждал поток: %d ms%n", waited);
        System.out.printf("разрешений свободно в конце: %d из %d (утечек нет)%n",
                bulkhead.availablePermits(), MAX_TO_PSP);
        System.out.printf("пул завершился: %s%n", finished);
        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - к PSP уходит не больше " + MAX_TO_PSP + " вызовов разом,");
        System.out.println("    остальные " + POOL + "-" + MAX_TO_PSP + " потоков остаются сервису");
        System.out.println("  - отказ занимает " + WAIT_MS + " ms вместо " + PSP_MS + " ms —");
        System.out.println("    поток возвращается в пул в 4 раза быстрее");
        System.out.println("  - деградация локальна: платежи придушены, баланс работает");
        System.out.println("  - release() в finally: иначе разрешения утекут и переборка");
        System.out.println("    закроется навсегда — сравни 'разрешений свободно в конце'");
        System.out.println("  - в проде: Resilience4j @Bulkhead (семафорный) или");
        System.out.println("    @Bulkhead(type = THREADPOOL) — отдельный пул на зависимость");
    }
}
