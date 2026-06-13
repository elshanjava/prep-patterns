package structural.adapter.bad;

import java.math.BigDecimal;

public class BadAdapterDemo {
    public static void main(String[] args) {
        System.out.println("== Adapter [BAD] ==");

        var service = new BadPricingService();

        System.out.println("quote 10 EUR→USD: " + service.quote(new BigDecimal("10.00"), "USD"));
        System.out.println("fee   20 USD→EUR: " + service.fee(new BigDecimal("20.00")));

        // Выглядит как работает — но SDK-специфичный код рассыпан по всему приложению.
        // Задача: нужна валюта GBP. Добавишь в 15 местах или только в адаптере?
    }
}
