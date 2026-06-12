package structural.adapter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * «Старый» SDK с неудобным API: строковые коды валют, центы как double.
 * Менять его мы не можем (внешняя/легаси-библиотека) — поэтому оборачиваем
 * адаптером, а не правим вызывающий код под него.
 */
class LegacyFxSdk {
    BigDecimal convertMoney(String fromCcy, String toCcy, double cents) {
        return BigDecimal.valueOf(cents * rate(fromCcy, toCcy))
                         .setScale(0, RoundingMode.HALF_UP);   // центы — целые
    }

    private double rate(String from, String to) {
        if (from.equals(to)) return 1.0;
        if (from.equals("EUR") && to.equals("USD")) return 1.08;
        if (from.equals("USD") && to.equals("EUR")) return 0.92;
        return 1.0;   // заглушка для остальных пар
    }
}
