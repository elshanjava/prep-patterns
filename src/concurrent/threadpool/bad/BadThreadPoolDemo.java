package concurrent.threadpool.bad;

import java.util.List;
import java.util.stream.LongStream;

public class BadThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== ThreadPool [BAD] — new Thread() на каждый платёж ==");

        var processor = new BadPaymentProcessor();
        List<Long> amounts = LongStream.rangeClosed(1, 20).boxed().map(i -> i * 100L).toList();

        long start = System.currentTimeMillis();
        processor.processBatch(amounts);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println();
        System.out.printf("платежей:        %d%n", amounts.size());
        System.out.printf("создано потоков: %d (по одному на платёж)%n", BadPaymentProcessor.createdThreads.get());
        System.out.printf("время:           %d ms%n", elapsed);

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - 20 платежей → 20 потоков; при 10 000 платежей → OOM (~10 GB стек)");
        System.out.println("  - нет контроля над параллелизмом: пиковая нагрузка неограниченна");
        System.out.println("  - нет Future: нельзя получить результат или обработать ошибку");
        System.out.println("  - нет graceful shutdown: потоки-зомби при остановке приложения");
    }
}
