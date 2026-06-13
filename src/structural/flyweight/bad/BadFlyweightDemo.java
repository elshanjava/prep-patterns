package structural.flyweight.bad;

import java.math.BigDecimal;

public class BadFlyweightDemo {
    public static void main(String[] args) {
        System.out.println("== Flyweight [BAD] — новый объект на каждую сделку ==");

        int tradeCount = 1_000_000;

        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BadTrade[] trades = new BadTrade[tradeCount];
        for (int i = 0; i < tradeCount; i++) {
            // 3 варианта валюты циклически — 3 разных «тяжёлых» объекта на каждую
            String ccy = switch (i % 3) { case 0 -> "USD"; case 1 -> "EUR"; default -> "GBP"; };
            trades[i] = new BadTrade(i, new BadCurrency(ccy), new BigDecimal("100"));
        }

        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.printf("trades:           %,d%n", tradeCount);
        System.out.printf("currency objects: %,d (на каждую сделку — свой)%n", tradeCount);
        System.out.printf("heap delta:       ~%,d KB%n", (after - before) / 1024);
        System.out.println("USD[0] == USD[3]? " + (trades[0].currency() == trades[3].currency())); // false
        System.out.println("EUR[1] == EUR[4]? " + (trades[1].currency() == trades[4].currency())); // false

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  1 000 000 сделок = 1 000 000 Currency-объектов (333К USD, 333К EUR, 334К GBP)");
        System.out.println("  Каждый содержит одинаковые данные — дублирование в памяти");
        System.out.println("  Сравнение == всегда false — нельзя использовать identity-кэши");
        System.out.println("  GC давление: 1М коротко-живущих объектов → частые minor GC");
    }
}
