package concurrent.threadpool.good;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Пул, собранный руками, — с ОГРАНИЧЕННОЙ очередью и настоящим backpressure.
//
// Зачем отдельно от ThreadPoolDemo: тот берёт Executors.newFixedThreadPool(), а у него
// LinkedBlockingQueue БЕЗ предела. Она не бывает полной, значит submit() не блокируется
// никогда, backpressure нет вовсе, и под нагрузкой очередь растёт до OOM.
// Ограничение очереди доступно только через конструктор ThreadPoolExecutor напрямую.
//
// ВАЖНО: это НЕ producer-consumer. Через границу летит Runnable (поведение), а не данные,
// и консьюмера здесь не пишет никто — его роль исполняют воркеры пула. Настоящий
// producer-consumer на пуле см. в producerconsumer/good/ExecutorConsumerDemo.
public class BoundedPoolDemo {

    private static final int WORKERS  = 2;
    private static final int CAPACITY = 5;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ThreadPool [GOOD] — ограниченная очередь + CallerRunsPolicy ==");

        Thread mainThread = Thread.currentThread();

        var pool = new ThreadPoolExecutor(
                WORKERS, WORKERS,
                0L, TimeUnit.MILLISECONDS,      // keepAlive не влияет: core == max
                // ArrayBlockingQueue: ёмкость обязательна по конструктору — забыть нельзя
                new ArrayBlockingQueue<Runnable>(CAPACITY),
                // без политики отказ был бы RejectedExecutionException (AbortPolicy),
                // а так вызывающий выполняет задачу сам и тем самым притормаживает себя
                new ThreadPoolExecutor.CallerRunsPolicy());

        var processed = new AtomicInteger();
        var byCaller  = new AtomicInteger();
        var maxQueue  = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            String id = "pay-" + i;
            pool.execute(() -> {
                // сравнение по ССЫЛКЕ, а не по имени потока: имя "main" под JUnit другое
                if (Thread.currentThread() == mainThread) byCaller.incrementAndGet();
                try {
                    Thread.sleep(50);                            // обработка платежа
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("  [" + Thread.currentThread().getName() + "] processed " + id);
                processed.incrementAndGet();
            });
            // очередь растёт ТОЛЬКО здесь, значит любой её максимум достигается сразу
            // после execute — замер корректен (занизить может, завысить нет)
            maxQueue.updateAndGet(cur -> Math.max(cur, pool.getQueue().size()));
            Thread.sleep(10);                                    // producer быстрее воркеров
        }

        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);

        long elapsed = System.currentTimeMillis() - start;

        System.out.println();
        System.out.printf("обработано платежей: %d из 10 (пул завершился штатно: %s)%n",
                processed.get(), finished);
        System.out.printf("макс. размер очереди: %d (capacity=%d — упёрлись в потолок)%n",
                maxQueue.get(), CAPACITY);
        System.out.printf("задач выполнил сам вызывающий: %d (CallerRunsPolicy притормозил его)%n",
                byCaller.get());
        System.out.printf("суммарное время: %d ms%n", elapsed);
        System.out.println();
        System.out.println("Что здесь видно:");
        System.out.println("  - порядок роста пула: core -> ОЧЕРЕДЬ -> max -> отказ;");
        System.out.println("    очередь наполняется РАНЬШЕ, чем пул растёт, поэтому");
        System.out.println("    newFixedThreadPool с безграничной очередью не растёт никогда");
        System.out.println("  - backpressure наглостью: вызывающего тормозят, ЗАГРУЖАЯ его работой");
        System.out.println("  - порядок при этом ломается: задача от CallerRunsPolicy минует очередь");
        System.out.println("  - CallerRunsPolicy ПОСЛЕ shutdown() молча выбрасывает задачу:");
        System.out.println("    в её коде if (!e.isShutdown()) r.run(); — ветки else нет");
        System.out.println("  - shutdownNow() вернул бы List<Runnable> невыполненного —");
        System.out.println("    его можно переложить, а не потерять");
        System.out.println("  - в Spring: ThreadPoolTaskExecutor, у него queueCapacity");
        System.out.println("    по умолчанию Integer.MAX_VALUE — та же мина");
    }
}
