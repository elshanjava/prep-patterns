package structural.flyweight.good;

import java.math.BigDecimal;

public class FlyweightDemo {
    public static void main(String[] args) {
        System.out.println("== Flyweight [GOOD] — один объект Currency на все сделки с одной валютой ==");

        int tradeCount = 1_000_000;

        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Trade[] trades = new Trade[tradeCount];
        for (int i = 0; i < tradeCount; i++) {
            // Currency.of("USD") возвращает один и тот же объект из пула
            trades[i] = new Trade(i, Currency.of("USD"), new BigDecimal("100"));
        }

        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.printf("trades:           %,d%n", tradeCount);
        System.out.printf("currency objects: 1 (все сделки разделяют один экземпляр)%n");
        System.out.printf("heap delta:       ~%,d KB%n", (after - before) / 1024);
        System.out.println("same instance?    " + (trades[0].currency() == trades[1].currency())); // true

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - было: 1_000_000 Currency-объектов (одинаковые данные в памяти 1M раз)");
        System.out.println("  - стало: 1 Currency.USD, независимо от числа сделок");
        System.out.println("  - пул потокобезопасен: ConcurrentHashMap.computeIfAbsent()");
        System.out.println("  - same instance? true — все сделки указывают на один объект");
    }
}
