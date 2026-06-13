package structural.facade.model;

import java.math.BigDecimal;
import java.util.Currency;

public final class FxService {
    public BigDecimal rate(Currency currency) {
        System.out.println("  [fx] rate for " + currency.getCurrencyCode());
        return new BigDecimal("1.00");
    }
}
