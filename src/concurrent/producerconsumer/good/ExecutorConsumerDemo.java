package concurrent.producerconsumer.good;

import concurrent.producerconsumer.model.Payment;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Producer-consumer, где ExecutorService заменяет ТОЛЬКО new Thread(...).
// Очередь (PaymentPipeline) остаётся своей, консьюмер остаётся своим — он сам решает,
// что делать с данными. Через границу летят ДАННЫЕ (Payment), а не задачи (Runnable);
// именно это отличает паттерн от простой отправки задач в пул (см. threadpool/BoundedPoolDemo).
//
// Главный вывод демки: shutdown() ядовитую пилюлю НЕ заменяет.
// Консьюмер — бесконечный цикл на take(); "доиграть до конца" ему нечего,
// поэтому awaitTermination честно возвращает false (фаза 2).
public class ExecutorConsumerDemo {

    private static final int CONSUMERS = 2;

    public static void main(String[] args) throws InterruptedException {
        withPoisonPills();
        withoutPoisonPills();

        System.out.println();
        System.out.println("Итог:");
        System.out.println("  - пул заменил new Thread(): не надо держать List<Thread> и join'ить");
        System.out.println("  - очередь и консьюмер остались своими — это и есть паттерн");
        System.out.println("  - shutdown() = 'новых не принимаю, запущенные доиграют';");
        System.out.println("    у бесконечного цикла доигрывать нечего -> он не завершится");
        System.out.println("  - пилюля нужна именно для того, чего shutdown() не умеет:");
        System.out.println("    дать консьюмеру ДОЕСТЬ очередь и выйти самому");
        System.out.println("  - shutdownNow() прервёт take(), но всё недоеденное будет потеряно");
        System.out.println("  - в Spring: @RabbitListener живёт так же — контейнер шлёт");
        System.out.println("    сигнал остановки, а не убивает поток на середине сообщения");
    }

    // ---------- ФАЗА 1: пилюли — консьюмеры выходят сами ----------
    private static void withPoisonPills() throws InterruptedException {
        System.out.println("== ФАЗА 1: пилюли + shutdown() ==");

        var pipeline  = new PaymentPipeline();
        var pool      = Executors.newFixedThreadPool(CONSUMERS);
        var processed = new AtomicInteger();

        for (int c = 1; c <= CONSUMERS; c++) {
            pool.execute(consumer(pipeline, processed, null));
        }

        for (int i = 1; i <= 10; i++) {
            pipeline.produce(new Payment("pay-" + i, i * 100L));
            Thread.sleep(10);                       // producer быстрее консьюмеров
        }
        pipeline.shutdown(CONSUMERS);               // по пилюле на каждого

        pool.shutdown();
        boolean finished = pool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.printf("  -> обработано %d из 10, пул завершился штатно: %s%n",
                processed.get(), finished);
    }

    // ---------- ФАЗА 2: без пилюль — shutdown() бессилен ----------
    private static void withoutPoisonPills() throws InterruptedException {
        System.out.println();
        System.out.println("== ФАЗА 2: тот же код, но БЕЗ пилюль ==");

        var pipeline  = new PaymentPipeline();
        var pool      = Executors.newFixedThreadPool(CONSUMERS);
        var processed = new AtomicInteger();
        var allDone   = new CountDownLatch(4);      // ждём, пока ВСЯ работа доедена

        for (int c = 1; c <= CONSUMERS; c++) {
            pool.execute(consumer(pipeline, processed, allDone));
        }

        for (int i = 1; i <= 4; i++) {
            pipeline.produce(new Payment("pay-" + i, i * 100L));
        }
        allDone.await();                            // работы больше нет, очередь пуста

        // Оба консьюмера сейчас спят в take(). Задач в очереди пула тоже нет.
        // Тем не менее пул НЕ завершится: запущенные задачи не возвращаются.
        pool.shutdown();
        boolean finished = pool.awaitTermination(300, TimeUnit.MILLISECONDS);
        System.out.printf("  -> вся работа сделана (%d из 4), очередь пуста (size=%d),%n",
                processed.get(), pipeline.size());
        System.out.printf("     но awaitTermination вернул: %s — консьюмеры висят на take()%n",
                finished);

        var abandoned = pool.shutdownNow();         // единственный выход — прерывание
        boolean afterNow = pool.awaitTermination(1, TimeUnit.SECONDS);
        System.out.printf("  -> shutdownNow(): прервал, вернул %d невыполненных задач, завершился: %s%n",
                abandoned.size(), afterNow);
    }

    // Один и тот же консьюмер для обеих фаз.
    private static Runnable consumer(PaymentPipeline pipeline, AtomicInteger processed,
                                     CountDownLatch allDone) {
        return () -> {
            String me = Thread.currentThread().getName();
            try {
                while (true) {
                    Payment p = pipeline.consume();
                    if (p == PaymentPipeline.POISON) {          // именно ==, не equals
                        System.out.println("  [" + me + "] пилюля -> выхожу");
                        break;
                    }
                    System.out.println("  [" + me + "] processed " + p.id());
                    processed.incrementAndGet();
                    Thread.sleep(50);
                    if (allDone != null) allDone.countDown();
                }
            } catch (InterruptedException e) {
                // Прервали на take() — восстанавливаем флаг и выходим из цикла.
                Thread.currentThread().interrupt();
                System.out.println("  [" + me + "] прерван на take() -> выхожу");
            }
        };
    }
}
