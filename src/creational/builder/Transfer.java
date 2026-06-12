package creational.builder;

public final class Transfer {                 // иммутабельный: все поля final
    private final String from;
    private final String to;
    private final long amount;
    private final String reference;           // опциональное
    private final String idempotencyKey;      // опциональное
 
    private Transfer(Builder b) {             // приватный конструктор
        this.from = b.from;
        this.to = b.to;
        this.amount = b.amount;
        this.reference = b.reference;
        this.idempotencyKey = b.idempotencyKey;
    }
    public static Builder builder() { return new Builder(); }

    @Override public String toString() {
        return "Transfer{from=" + from + ", to=" + to + ", amount=" + amount +
               ", reference=" + reference + ", idempotencyKey=" + idempotencyKey + "}";
    }

    // Внутренний мутабельный «накопитель» — собирает поля по одному (fluent),
    // а build() атомарно проверяет инварианты и отдаёт иммутабельный объект.
    public static final class Builder {
        private String from, to, reference, idempotencyKey;
        private long amount;
        public Builder from(String v)   { this.from = v; return this; }   // fluent
        public Builder to(String v)     { this.to = v; return this; }
        public Builder amount(long v)   { this.amount = v; return this; }
        public Builder reference(String v)      { this.reference = v; return this; }
        public Builder idempotencyKey(String v) { this.idempotencyKey = v; return this; }
 
        public Transfer build() {
            // ЕДИНАЯ точка валидации инвариантов — до создания объекта
            if (from == null || to == null)
                throw new IllegalStateException("from/to обязательны");
            if (amount <= 0)
                throw new IllegalStateException("amount должен быть > 0");
            return new Transfer(this);
        }
    }
}
// Transfer.builder().from("A").to("B").amount(100).reference("inv-42").build();
