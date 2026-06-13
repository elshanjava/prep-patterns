package creational.builder.good;

// Читаемый, иммутабельный объект — без телескопических конструкторов.
// Обязательные поля — в builder(...), опциональные — именованными методами.
// Валидация сосредоточена в одном месте: Builder.build().
public final class Transfer {
    private final String from;
    private final String to;
    private final long   amount;
    private final String currency;
    private final String reference;
    private final String idempotencyKey;

    private Transfer(Builder b) {
        this.from           = b.from;
        this.to             = b.to;
        this.amount         = b.amount;
        this.currency       = b.currency;
        this.reference      = b.reference;
        this.idempotencyKey = b.idempotencyKey;
    }

    public String from()           { return from; }
    public String to()             { return to; }
    public long   amount()         { return amount; }
    public String currency()       { return currency; }
    public String reference()      { return reference; }
    public String idempotencyKey() { return idempotencyKey; }

    @Override public String toString() {
        return "Transfer{from=" + from + ", to=" + to + ", amount=" + amount
               + ", currency=" + currency + ", ref=" + reference + ", key=" + idempotencyKey + "}";
    }

    public static Builder builder(String from, String to, long amount) {
        return new Builder(from, to, amount);
    }

    public static final class Builder {
        private final String from;
        private final String to;
        private final long   amount;
        private String currency       = "EUR";  // разумный дефолт
        private String reference;
        private String idempotencyKey;

        private Builder(String from, String to, long amount) {
            if (from == null || to == null) throw new IllegalArgumentException("from/to обязательны");
            if (amount <= 0)               throw new IllegalArgumentException("amount > 0");
            this.from   = from;
            this.to     = to;
            this.amount = amount;
        }

        public Builder currency(String c)       { this.currency       = c; return this; }
        public Builder reference(String r)      { this.reference      = r; return this; }
        public Builder idempotencyKey(String k) { this.idempotencyKey = k; return this; }

        public Transfer build() { return new Transfer(this); }
    }
}
