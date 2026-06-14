package concurrent.threadpool.bad;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Новый Thread на каждый платёж.
// 1000 платежей = 1000 потоков: stack memory ~1MB каждый → ~1GB только на стеки.
// Context-switching между 1000 потоками убивает throughput.
// Нет контроля над параллелизмом: пик нагрузки → OOM без предупреждения.
final class BadPaymentProcessor {
    static final AtomicInteger createdThreads = new AtomicInteger();

    void process(long amountCents) {
        Thread t = new Thread(() -> {
            try { Thread.sleep(10); } catch (InterruptedException ignored) {}
            System.out.println("  [thread-" + Thread.currentThread().threadId() + "] processed " + amountCents);
        });
        createdThreads.incrementAndGet();
        t.start();
    }

    void processBatch(List<Long> amounts) throws InterruptedException {
        // Создаём поток на каждый элемент — никакого ограничения
        amounts.forEach(this::process);
        Thread.sleep(200); // ждём завершения (нет Future — нет контроля)
    }
}
