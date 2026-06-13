package structural.adapter.good;

import structural.adapter.model.LegacyFxSdk;

import java.math.BigDecimal;

public class AdapterDemo {
    public static void main(String[] args) {
        System.out.println("== Adapter [GOOD] — LegacyFxSdk скрыт за CurrencyConverter ==");

        CurrencyConverter adapter = new LegacyFxAdapter(new LegacyFxSdk());
        PricingService    pricing = new PricingService(adapter);

        var quote = pricing.quote(new BigDecimal("100.00"), "USD");
        System.out.println("quote EUR→USD: " + quote);

        var fee = pricing.fee(new BigDecimal("200.00"));
        System.out.println("fee   USD→EUR: " + fee);

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - PricingService не знает о double, movePointLeft, строковых кодах");
        System.out.println("  - дублированная конвертация центов исчезла — живёт в одном адаптере");
        System.out.println("  - SDK изменит double → long: правим только LegacyFxAdapter");
        System.out.println("  - в тестах: new PricingService(mockConverter) — без реального SDK");
    }
}
