package structural.adapter.bad;

import java.math.BigDecimal;

public class BadAdapterDemo {
    public static void main(String[] args) {
        System.out.println("== Adapter [BAD] — double↔BigDecimal boilerplate в 5 местах ==");
        System.out.println();

        var svc = new BadPricingService();

        System.out.printf("quote   EUR→USD 100:    %s%n", svc.quote(new BigDecimal("100.00"), "USD"));
        System.out.printf("fee     USD 200:         %s%n", svc.fee(new BigDecimal("200.00")));
        System.out.printf("hedge   GBP→USD 500:     %s%n", svc.hedgeExposure(new BigDecimal("500.00")));
        System.out.printf("payout  EUR→EUR 300:     %s%n", svc.convertForPayout(new BigDecimal("300.00"), "EUR"));
        System.out.printf("mtm     USD→EUR 1000:    %s%n", svc.markToMarket(new BigDecimal("1000.00")));

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  5 методов = 5 копий: .multiply(100).doubleValue() ... .movePointLeft(2)");
        System.out.println("  SDK сменит double → long: найди и исправь 5 методов вручную");
        System.out.println("  Тесты: нельзя подменить SDK — он hardcoded через new LegacyFxSdk()");
        System.out.println("  Добавить метод convertForHedge(): copy-paste boilerplate в 6-й раз");
    }
}
