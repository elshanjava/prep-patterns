package structural.flyweight.bad;

import java.math.BigDecimal;

public class BadFlyweightDemo {
    public static void main(String[] args) {
        System.out.println("== Flyweight [BAD] — новый объект на каждую сделку ==");

        int tradeCount = 1_000_000;

        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BadTrade[] trades = new BadTrade[tradeCount];
        for (int i = 0; i < tradeCount; i++) {
            // каждая сделка создаёт свой объект BadCurrency — даже если данные одни и те же
            trades[i] = new BadTrade(i, new BadCurrency("USD"), new BigDecimal("100"));
        }

        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.printf("trades:          %,d%n", tradeCount);
        System.out.printf("currency objects: %,d (по одному на сделку)%n", tradeCount);
        System.out.printf("heap delta:       ~%,d KB%n", (after - before) / 1024);
        System.out.println("same data?        " + (trades[0].currency() == trades[1].currency())); // false
    }
}
