package creational.builder.bad;

// Telescoping constructors: с каждым новым опциональным полем количество
// конструкторов взрывается. Вызов new BadTransfer("A","B",100,null,"key-1")
// выглядит нечитаемо — что такое null? который по счёту аргумент это reference?
final class BadTransfer {
    final String from;
    final String to;
    final long   amount;
    final String currency;
    final String reference;
    final String idempotencyKey;

    // Конструктор 1: только обязательное
    BadTransfer(String from, String to, long amount) {
        this(from, to, amount, "EUR", null, null);
    }

    // Конструктор 2: с валютой
    BadTransfer(String from, String to, long amount, String currency) {
        this(from, to, amount, currency, null, null);
    }

    // Конструктор 3: с reference
    BadTransfer(String from, String to, long amount, String currency, String reference) {
        this(from, to, amount, currency, reference, null);
    }

    // Конструктор 4: полный — легко перепутать порядок аргументов
    BadTransfer(String from, String to, long amount,
                String currency, String reference, String idempotencyKey) {
        if (from == null || to == null) throw new IllegalArgumentException("from/to обязательны");
        if (amount <= 0)               throw new IllegalArgumentException("amount > 0");
        this.from           = from;
        this.to             = to;
        this.amount         = amount;
        this.currency       = currency;
        this.reference      = reference;
        this.idempotencyKey = idempotencyKey;
    }

    @Override public String toString() {
        return "BadTransfer{from=" + from + ", to=" + to + ", amount=" + amount
               + ", currency=" + currency + ", ref=" + reference + ", key=" + idempotencyKey + "}";
    }
}
