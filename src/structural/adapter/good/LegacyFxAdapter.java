package structural.adapter.good;

import structural.adapter.model.LegacyFxSdk;

import java.math.BigDecimal;
import java.util.Currency;

// Адаптер: вся конвертация double ↔ BigDecimal сосредоточена здесь — один раз.
// Клиенты не знают о LegacyFxSdk, double-центах и строковых кодах валют.
// SDK изменит API? — правим только этот класс, а не каждый вызов по кодовой базе.
final class LegacyFxAdapter implements CurrencyConverter {
    private final LegacyFxSdk sdk;

    LegacyFxAdapter(LegacyFxSdk sdk) { this.sdk = sdk; }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {
        double cents = amount.movePointRight(2).doubleValue();
        BigDecimal resultCents = sdk.convertMoney(
                from.getCurrencyCode(), to.getCurrencyCode(), cents);
        return resultCents.movePointLeft(2);
    }
}
