package structural.adapter.good;

import java.math.BigDecimal;
import java.util.Currency;

// Те же 5 методов — ни одного double, ни одного movePointLeft.
// Вся конвертация делегирована адаптеру, здесь только бизнес-логика.
final class PricingService {
    private final CurrencyConverter converter;

    PricingService(CurrencyConverter converter) { this.converter = converter; }

    BigDecimal quote(BigDecimal eurAmount, String targetCode) {
        return converter.convert(eurAmount, eur(), ccy(targetCode));
    }

    BigDecimal fee(BigDecimal usdAmount) {
        return converter.convert(usdAmount, usd(), eur())
                        .multiply(new BigDecimal("0.029"));
    }

    BigDecimal hedgeExposure(BigDecimal gbpExposure) {
        return converter.convert(gbpExposure, ccy("GBP"), usd());
    }

    BigDecimal convertForPayout(BigDecimal amount, String fromCcy) {
        return converter.convert(amount, ccy(fromCcy), eur());
    }

    BigDecimal markToMarket(BigDecimal usdPosition) {
        return converter.convert(usdPosition, usd(), eur());
    }

    private static Currency eur()              { return Currency.getInstance("EUR"); }
    private static Currency usd()              { return Currency.getInstance("USD"); }
    private static Currency ccy(String code)   { return Currency.getInstance(code); }
}
