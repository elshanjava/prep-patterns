package structural.flyweight.good;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Легковес: один объект на код валюты, разделяется между всеми сделками.
// 1_000_000 сделок в USD → 1 объект в памяти, не 1_000_000.
final class Currency {
    private static final Map<String, Currency> POOL = new ConcurrentHashMap<>();

    private final String code;
    private final int    scale;
    private final String symbol;

    private Currency(String code) {
        this.code   = code;
        this.scale  = 2;
        this.symbol = switch (code) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            default    -> code;
        };
    }

    // Единственная точка доступа: возвращает существующий экземпляр или создаёт новый
    static Currency of(String code) {
        return POOL.computeIfAbsent(code, Currency::new);
    }

    String code()   { return code; }
    int    scale()  { return scale; }
    String symbol() { return symbol; }

    @Override public String toString() { return code + "(" + symbol + ")"; }
}
