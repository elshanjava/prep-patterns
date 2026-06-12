package structural.adapter;

import structural.adapter.model.Money;

import java.math.BigDecimal;
import java.util.Currency;

public final class LegacyFxAdapter implements CurrencyConverter {
    private final LegacyFxSdk sdk;                 // адаптируемый объект (composition)
 
    public LegacyFxAdapter(LegacyFxSdk sdk) {      // object adapter (через композицию)
        this.sdk = sdk;
    }
 
    @Override
    public Money convert(Money amount, Currency target) {
        // вся грязь преобразования заперта здесь
        BigDecimal result = sdk.convertMoney(
                amount.currency().getCurrencyCode(),
                target.getCurrencyCode(),
                amount.toCents());                 // Money сам отдаёт центы, без double
        return Money.ofCents(result, target);
    }
}
