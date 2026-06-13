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
            int threads = 50;
            Set<Config> seen = ConcurrentHashMap.newKeySet();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) pool.submit(() -> seen.add(Config.getInstance()));
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("  потоков: " + threads + ", уникальных экземпляров: " + seen.size());
            System.out.println("  apiUrl: " + Config.getInstance().apiUrl());
        }

        System.out.println("\n--- 2. Enum (невозможно создать второй через reflection или readObject) ---");
        {
            Config2 a = Config2.INSTANCE;
            Config2 b = Config2.INSTANCE;
            System.out.println("  INSTANCE == INSTANCE: " + (a == b));
            System.out.println("  apiKey: " + a.apiKey());
            // Reflection-атака: Constructor.newInstance() выбросит IllegalArgumentException
            // Десериализация: readResolve не нужен — JVM гарантирует единственность enum-констант
        }

        System.out.println("\n--- 3. DCL + volatile (явный, понятный контроль над локом) ---");
        {
            int threads = 50;
            Set<Config3> seen = ConcurrentHashMap.newKeySet();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) pool.submit(() -> seen.add(Config3.getInstance()));
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("  потоков: " + threads + ", уникальных экземпляров: " + seen.size());
            System.out.println("  timeoutMs: " + Config3.getInstance().timeoutMs());
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  Holder:  JVM classloader гарантирует единственность — volatile не нужен");
        System.out.println("  Enum:    защита от reflection и сериализации по спецификации JLS §8.9");
        System.out.println("  DCL:     volatile предотвращает публикацию частично инициализированного объекта");
        System.out.println("  Все три: 50 потоков, 1 экземпляр — детерминированно, не «в этот раз повезло»");
    }
}
