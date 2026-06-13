package creational.singleton.bad;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BadSingletonDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== Singleton [BAD] — race condition ==");

        // Симулируем параллельный старт 20 потоков (как в реальном Spring-приложении
        // когда несколько бинов инициализируются одновременно).
        Set<Integer> identities = ConcurrentHashMap.newKeySet();
        ExecutorService pool = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 200; i++) {
            pool.submit(() ->
                identities.add(System.identityHashCode(UnsafeConfig.getInstance())));
        }

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("unique instances seen: " + identities.size());
        System.out.println("expected: 1  |  actual may be > 1 due to race");
        System.out.println();
        System.out.println("Последствия в продакшне:");
        System.out.println("  - PaymentService читает apiKey из экземпляра A");
        System.out.println("  - FraudService читает apiKey из экземпляра B");
        System.out.println("  - состояние рассинхронизировано, баг неочевиден");
    }
}
