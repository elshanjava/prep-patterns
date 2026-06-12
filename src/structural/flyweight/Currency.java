package structural.flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Currency {                    // immutable
    private final String code;
    private final int scale;
    private final String symbol;
    private Currency(String code, int scale, String symbol) {
        this.code = code; this.scale = scale; this.symbol = symbol;
    }
 
    // Фабрика-пул: один экземпляр на код валюты
    private static final Map<String, Currency> POOL = new ConcurrentHashMap<>();
    public static Currency of(String code) {
        return POOL.computeIfAbsent(code, Currency::load);  // переиспользуем
    }

    // «Справочник»: тут был бы поход в БД/конфиг. Вызывается ОДИН раз на код.
    private static Currency load(String code) {
        String symbol = switch (code) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            default    -> code;
        };
        return new Currency(code, 2, symbol);
    }

    public String code()   { return code; }
    public int    scale()  { return scale; }
    public String symbol() { return symbol; }

    @Override public String toString() { return code + "(" + symbol + ")"; }
}
