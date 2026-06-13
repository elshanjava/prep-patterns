package creational.singleton.bad;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BadSingletonDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== Singleton [BAD] — два экземпляра с разными apiKey ==");
        System.out.println();

        // 50 потоков одновременно — как при параллельной инициализации Spring-бинов
        int threads = 50;
        Set<Integer> instanceIds = ConcurrentHashMap.newKeySet();
        Set<String>  apiKeys     = ConcurrentHashMap.newKeySet();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                UnsafeConfig cfg = UnsafeConfig.getInstance();
                instanceIds.add(cfg.instanceId);
                apiKeys.add(cfg.apiKey);
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("потоков запущено:      " + threads);
        System.out.println("конструкторов вызвано: " + UnsafeConfig.totalCreated());
        System.out.println("уникальных instanceId: " + instanceIds);
        System.out.println("уникальных apiKey:     " + apiKeys);
        System.out.println();

        if (UnsafeConfig.totalCreated() > 1) {
            System.out.println("ПРОБЛЕМА ВОСПРОИЗВЕДЕНА: " + UnsafeConfig.totalCreated() + " экземпляра");
            System.out.println("  PaymentService → sk_live_1, FraudService → sk_live_2");
            System.out.println("  PSP отклоняет часть запросов: ключ не совпадает с аккаунтом");
        } else {
            System.out.println("На этот раз повезло. Запусти ещё раз — результат непредсказуем.");
        }

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  volatile отсутствует: JIT публикует ссылку до завершения конструктора");
        System.out.println("  лок отсутствует: два потока проходят if(null) одновременно");
        System.out.println("  баг недетерминированный: в тестах один экземпляр, в проде — нет");
        System.out.println("  два синглтона с разными apiKey = рассинхронизированное состояние");
    }
}
