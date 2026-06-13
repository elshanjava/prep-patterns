package structural.adapter.good;

import java.math.BigDecimal;
import java.util.Currency;

// Клиент знает только о CurrencyConverter — не знает о LegacyFxSdk.
// Ручная конвертация центов исчезла: она живёт в адаптере, не здесь.
final class PricingService {
    private final CurrencyConverter converter;

    PricingService(CurrencyConverter converter) { this.converter = converter; }

    BigDecimal quote(BigDecimal eurAmount, String targetCode) {
        return converter.convert(eurAmount,
                Currency.getInstance("EUR"), Currency.getInstance(targetCode));
    }

    BigDecimal fee(BigDecimal usdAmount) {
        BigDecimal inEur = converter.convert(usdAmount,
                Currency.getInstance("USD"), Currency.getInstance("EUR"));
        return inEur.multiply(new BigDecimal("0.029"));
    }
}
