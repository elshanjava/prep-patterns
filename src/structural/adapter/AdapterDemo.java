package structural.adapter;

import structural.adapter.model.Money;

import java.math.BigDecimal;
import java.util.Currency;

// Запуск: клиент зовёт удобный convert(), адаптер транслирует это в кривой
// вызов легаси-SDK и обратно.
public class AdapterDemo {
    public static void main(String[] args) {
        System.out.println("== Adapter ==");
        Currency eur = Currency.getInstance("EUR");
        Currency usd = Currency.getInstance("USD");

        CurrencyConverter converter = new LegacyFxAdapter(new LegacyFxSdk());
        PricingService pricing = new PricingService(converter);

        Money in  = Money.of(new BigDecimal("10.00"), eur);
        Money out = pricing.quote(in, usd);
        System.out.println(in + " -> " + out);
    }
}
