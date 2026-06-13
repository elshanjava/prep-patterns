package structural.adapter.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Внешняя библиотека с неудобным API: double-центы, строковые коды валют.
// Менять её нельзя — она приходит как JAR от партнёра.
public final class LegacyFxSdk {
    public BigDecimal convertMoney(String fromCcy, String toCcy, double cents) {
        return BigDecimal.valueOf(cents * rate(fromCcy, toCcy))
                         .setScale(0, RoundingMode.HALF_UP);
    }

    private double rate(String from, String to) {
        if (from.equals(to))                                  return 1.0;
        if (from.equals("EUR") && to.equals("USD"))           return 1.08;
        if (from.equals("USD") && to.equals("EUR"))           return 0.92;
        return 1.0;
    }
}
