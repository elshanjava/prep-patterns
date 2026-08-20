package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Тот же конвейер, но без ручных потоков, без собственной очереди и без ядовитых пилюль.
//
//   очередь        -> внутренняя очередь ThreadPoolExecutor (ограниченная!)
//   backpressure   -> CallerRunsPolicy: захлебнулись — отправитель выполняет задачу сам
//   остановка      -> shutdown() + awaitTermination(): очередь ДОРАБАТЫВАЕТСЯ
//
// Именно так это пишут в проде: своя реализация нужна, только если пула по каким-то
// причинам нет. Сравни с ProducerConsumerDemo — там всё то же самое, но руками.
public class ExecutorPipelineDemo {

    private static final int WORKERS  = 2;
    private static final int CAPACITY = 5;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ProducerConsumer [GOOD] — ThreadPoolExecutor, остановка через shutdown() ==");

        var pool = new ThreadPoolExecutor(
                WORKERS,
                WORKERS,
                0L,
                TimeUnit.MILLISECONDS,
                // ArrayBlockingQueue: ёмкость обязательна по конструктору — забыть ограничить нельзя.
                // Фабрики Executors.* подсовывают LinkedBlockingQueue БЕЗ предела -> путь в OOM.
                new ArrayBlockingQueue<>(CAPACITY),
                // без этой политики ограниченная очередь давала бы не backpressure,
                // а RejectedExecutionException (Abort) или тихую потерю (Discard)
                new ThreadPoolExecutor.CallerRunsPolicy());

        var processed = new AtomicInteger();
        var byCaller  = new AtomicInteger();
        var maxQueue  = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 1; i <= 10; i++) {
            var payment = new Payment("pay-" + i, i * 100L);
            pool.execute(() -> {
                String me = Thread.currentThread().getName();
                if (me.equals("main")) byCaller.incrementAndGet();   // сработал CallerRunsPolicy
                try {
                    Thread.sleep(50);                                // обработка платежа
                    if (me.equals("main")) byCaller.incrementAndGet();     } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("  [" + me + "] processed " + payment.id());
                processed.incrementAndGet();
            });
            maxQueue.updateAndGet(cur -> Math.max(cur, pool.getQueue().size()));
            Thread.sleep(10);                                        // producer быстрее обработчиков
        }

        // Вместо ядовитых пилюль: новые задачи не принимаем, уже принятые доигрываем.
        // shutdownNow() повёл бы себя иначе — прервал бы работающие и вернул невыполненные.
        pool.shutdown();
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);

        long elapsed = System.currentTimeMillis() - start;

        System.out.println();
        System.out.printf("обработано платежей: %d из 10 (пул завершился штатно: %s)%n",
                processed.get(), finished);
        System.out.printf("макс. размер очереди: %d (capacity=%d — backpressure сработал)%n",
                maxQueue.get(), CAPACITY);
        System.out.printf("задач выполнил сам main: %d (CallerRunsPolicy притормозил producer'а)%n",
                byCaller.get());
        System.out.printf("суммарное время: %d ms%n", elapsed);
        System.out.println();
        System.out.println("Отличия от версии с ядовитой пилюлей:");
        System.out.println("  - нет своей очереди: её роль играет очередь пула");
        System.out.println("  - нет пилюль: не надо знать число обработчиков и класть по штуке на каждого");
        System.out.println("  - нет ручных Thread: пул сам их создаёт и переиспользует");
        System.out.println("  - shutdown() дорабатывает очередь, shutdownNow() прерывает и");
        System.out.println("    ВОЗВРАЩАЕТ список невыполненных задач — их можно переложить, а не потерять");
        System.out.println("  - в Spring: ThreadPoolTaskExecutor с queueCapacity + CallerRunsPolicy");
    }
}
