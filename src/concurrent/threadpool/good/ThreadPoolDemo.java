package concurrent.threadpool.good;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.LongStream;

public class ThreadPoolDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== ThreadPool [GOOD] — bounded ExecutorService ==");

        List<Long> amounts = LongStream.rangeClosed(1, 20).boxed().map(i -> i * 100L).toList();
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;

        long start = System.currentTimeMillis();
        try (var processor = new PaymentProcessor()) {
            List<Future<String>> futures = processor.processBatch(amounts);
            for (Future<String> f : futures) {
                System.out.println("  " + f.get());
            }
        }
        long elapsed = System.currentTimeMillis() - start;

        System.out.println();
        System.out.printf("платежей:        %d%n", amounts.size());
        System.out.printf("размер пула:     %d (availableProcessors * 2)%n", poolSize);
        System.out.printf("время:           %d ms%n", elapsed);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - 20 платежей → " + poolSize + " потоков максимум, не 20");
        System.out.println("  - Future: результат + обработка ошибок");
        System.out.println("  - backpressure: submit() блокируется если очередь полна");
        System.out.println("  - graceful shutdown через AutoCloseable");
        System.out.println("  - в Spring: @Async + ThreadPoolTaskExecutor — тот же паттерн");
    }
}
