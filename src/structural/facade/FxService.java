package structural.facade;

import java.math.BigDecimal;
import java.util.Currency;

// Подсистема №2: курсы валют.
final class FxService {
    BigDecimal rate(Currency currency) {
        System.out.println("  [fx] rate for " + currency.getCurrencyCode());
        return new BigDecimal("1.00");
    }
}
