package creational.singleton.good;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingletonDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== Singleton [GOOD] — три потокобезопасных варианта ==");

        System.out.println("\n--- 1. Holder (рекомендуемый: lazy + без синхронизации на горячем пути) ---");
        {
            int threads = 20;
            Set<Config> seen = ConcurrentHashMap.newKeySet();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) pool.submit(() -> seen.add(Config.getInstance()));
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("  потоков: " + threads + ", уникальных экземпляров: " + seen.size());
        }

        System.out.println("\n--- 2. Enum (защита от reflection + корректная десериализация) ---");
        Config2 a = Config2.INSTANCE;
        Config2 b = Config2.INSTANCE;
        System.out.println("  INSTANCE == INSTANCE: " + (a == b));
        System.out.println("  url: " + a.apiUrl());

        System.out.println("\n--- 3. DCL + volatile (явный контроль, понятно читается) ---");
        {
            int threads = 20;
            Set<Config3> seen = ConcurrentHashMap.newKeySet();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) pool.submit(() -> seen.add(Config3.getInstance()));
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("  потоков: " + threads + ", уникальных экземпляров: " + seen.size());
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  Holder: потокобезопасно без явных локов, lazy-инициализация бесплатна");
        System.out.println("  Enum:   невозможно создать второй экземпляр через reflection или readObject()");
        System.out.println("  DCL:    volatile предотвращает reordering JIT — объект всегда полностью построен");
    }
}
