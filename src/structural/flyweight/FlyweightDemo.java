package structural.flyweight;

import java.math.BigDecimal;

// Запуск: общее (внутреннее) состояние валюты хранится в ОДНОМ разделяемом
// объекте из пула, а уникальное (id, сумма) — снаружи, в каждой сделке.
public class FlyweightDemo {
    public static void main(String[] args) {
        System.out.println("== Flyweight ==");

        Currency usd1 = Currency.of("USD");
        Currency usd2 = Currency.of("USD");           // тот же объект из пула
        System.out.println("один экземпляр на код: " + (usd1 == usd2));

        Trade t1 = new Trade(1, usd1, new BigDecimal("100"));
        Trade t2 = new Trade(2, Currency.of("USD"), new BigDecimal("250"));
        System.out.println("сделки делят валюту: " + (t1.currency() == t2.currency())
                           + " -> " + t1.currency());
    }
}
