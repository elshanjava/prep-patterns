package structural.adapter.good;

import java.math.BigDecimal;
import java.util.Currency;

// Чистый интерфейс для конвертации: работает с типами Java (BigDecimal, Currency),
// а не с double-центами и строками как LegacyFxSdk.
interface CurrencyConverter {
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
}
