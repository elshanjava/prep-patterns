package structural.adapter.good;

import structural.adapter.model.LegacyFxSdk;

import java.math.BigDecimal;

public class AdapterDemo {
    public static void main(String[] args) {
        System.out.println("== Adapter [GOOD] — весь boilerplate в одном адаптере ==");
        System.out.println();

        CurrencyConverter adapter = new LegacyFxAdapter(new LegacyFxSdk());
        PricingService    svc     = new PricingService(adapter);

        System.out.printf("quote   EUR→USD 100:    %s%n", svc.quote(new BigDecimal("100.00"), "USD"));
        System.out.printf("fee     USD 200:         %s%n", svc.fee(new BigDecimal("200.00")));
        System.out.printf("hedge   GBP→USD 500:     %s%n", svc.hedgeExposure(new BigDecimal("500.00")));
        System.out.printf("payout  EUR→EUR 300:     %s%n", svc.convertForPayout(new BigDecimal("300.00"), "EUR"));
        System.out.printf("mtm     USD→EUR 1000:    %s%n", svc.markToMarket(new BigDecimal("1000.00")));

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  Весь double↔BigDecimal boilerplate: 1 место (LegacyFxAdapter.convert())");
        System.out.println("  SDK сменит double → long: правим только LegacyFxAdapter, не 5 методов");
        System.out.println("  В тестах: new PricingService(mockConverter) — без реального SDK");
        System.out.println("  Добавить метод convertForHedge(): 1 строка в PricingService, 0 boilerplate");
    }
}
