package concurrent.bulkhead.bad;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Без переборки: медленный PSP забирает ВЕСЬ общий пул потоков.
// Страдает не только оплата — страдает всё, что делит с ней пул.
// Это и есть каскадный отказ: одна зависимость легла, сервис лёг целиком.
public class BadBulkheadDemo {

    private static final int  POOL     = 10;    // общий пул сервиса
    private static final int  REQUESTS = 20;    // запросов к тормозящему PSP
    private static final long PSP_MS   = 200;   // PSP отвечает медленно

    public static void main(String[] args) throws Exception {
        System.out.println("== Bulkhead [BAD] — ограничений нет, PSP съедает весь пул ==");

        ExecutorService pool = Executors.newFixedThreadPool(POOL);
        var inFlight    = new AtomicInteger();
        var maxInFlight = new AtomicInteger();

        for (int i = 0; i < REQUESTS; i++) {
            pool.execute(() -> {
                int n = inFlight.incrementAndGet();
                maxInFlight.accumulateAndGet(n, Math::max);
                try {
                    Thread.sleep(PSP_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    inFlight.decrementAndGet();
                }
            });
        }

        // Обычный быстрый запрос, к PSP отношения не имеющий: "покажи баланс".
        // Меряем, сколько он БУДЕТ ЖДАТЬ, прежде чем ему вообще дадут поток.
        long submittedAt = System.currentTimeMillis();
        var  startedAt   = new CompletableFuture<Long>();
        pool.execute(() -> startedAt.complete(System.currentTimeMillis()));

        long waited = startedAt.get() - submittedAt;

        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println();
        System.out.printf("одновременных вызовов к PSP: %d (пул из %d занят целиком)%n",
                maxInFlight.get(), POOL);
        System.out.printf("быстрый запрос 'покажи баланс' ждал поток: %d ms%n", waited);
        System.out.printf("пул завершился: %s%n", finished);
        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - PSP тормозит -> все " + POOL + " потоков заняты ожиданием сети");
        System.out.println("  - запрос баланса, который выполняется за 1 ms, стоит в очереди");
        System.out.println("  - деградация не локальна: лёг PSP — лёг весь сервис");
        System.out.println("  - добавить потоков не выход: PSP тормозит, очередь всё равно растёт");
    }
}
