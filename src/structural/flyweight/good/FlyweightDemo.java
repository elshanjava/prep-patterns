package structural.flyweight.good;

import java.math.BigDecimal;

public class FlyweightDemo {
    public static void main(String[] args) {
        System.out.println("== Flyweight [GOOD] — пул: 3 объекта вместо 1 000 000 ==");

        int tradeCount = 1_000_000;

        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Trade[] trades = new Trade[tradeCount];
        for (int i = 0; i < tradeCount; i++) {
            String ccy = switch (i % 3) { case 0 -> "USD"; case 1 -> "EUR"; default -> "GBP"; };
            trades[i] = new Trade(i, Currency.of(ccy), new BigDecimal("100"));
        }

        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.printf("trades:           %,d%n", tradeCount);
        System.out.printf("currency objects: 3 (USD, EUR, GBP) — пул размером 3, не %,d%n", tradeCount);
        System.out.printf("heap delta:       ~%,d KB%n", (after - before) / 1024);
        System.out.println("USD[0] == USD[3]? " + (trades[0].currency() == trades[3].currency())); // true!
        System.out.println("EUR[1] == EUR[4]? " + (trades[1].currency() == trades[4].currency())); // true!

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  было: 1 000 000 Currency-объектов → стало: 3 (по одному на код валюты)");
        System.out.println("  == сравнение работает: trades[0].currency() == trades[3].currency() → true");
        System.out.println("  меньше GC pressure: 3 long-lived объекта вместо 1М короткоживущих");
        System.out.println("  пул потокобезопасен: ConcurrentHashMap.computeIfAbsent()");
        System.out.println("  добавить CHF: Currency.of(\"CHF\") → автоматически в пул, 0 изменений");
    }
}
