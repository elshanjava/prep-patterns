package structural.flyweight.bad;

// Тяжёлый объект без пула: каждый вызов new BadCurrency(...) делает "дорогую" инициализацию.
// 10 млн сделок в USD = 10 млн одинаковых объектов с одинаковыми данными в памяти.
final class BadCurrency {
    private final String code;
    private final int    scale;
    private final String symbol;

    BadCurrency(String code) {
        // Симулируем «тяжёлую» загрузку: в реальности — поход в БД или конфиг-файл
        this.code   = code;
        this.scale  = 2;
        this.symbol = switch (code) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            default    -> code;
        };
        // System.out.println("  [BadCurrency] loading " + code + " from DB...");
    }

    public String code()   { return code; }
    public int    scale()  { return scale; }
    public String symbol() { return symbol; }

    @Override public String toString() { return code + "(" + symbol + ")"; }
}
