package creational.builder.good;

import java.time.Instant;

// Читаемый иммутабельный объект с именованными полями.
// Обязательные — в builder(...), опциональные — отдельными методами.
// Добавить 9-е поле: 1 метод в Builder, 0 изменений в существующих вызовах.
public final class Transfer {
    private final String  from;
    private final String  to;
    private final long    amountCents;
    private final String  currency;
    private final String  reference;
    private final String  idempotencyKey;
    private final Instant scheduledAt;
    private final int     maxRetries;

    private Transfer(Builder b) {
        this.from           = b.from;
        this.to             = b.to;
        this.amountCents    = b.amountCents;
        this.currency       = b.currency;
        this.reference      = b.reference;
        this.idempotencyKey = b.idempotencyKey;
        this.scheduledAt    = b.scheduledAt;
        this.maxRetries     = b.maxRetries;
    }

    public String  from()           { return from; }
    public String  to()             { return to; }
    public long    amountCents()    { return amountCents; }
    public String  currency()       { return currency; }
    public String  reference()      { return reference; }
    public String  idempotencyKey() { return idempotencyKey; }
    public Instant scheduledAt()    { return scheduledAt; }
    public int     maxRetries()     { return maxRetries; }

    @Override public String toString() {
        return "Transfer{from=" + from + ", to=" + to + ", amount=" + amountCents
               + " " + currency + ", ref=" + reference + ", key=" + idempotencyKey
               + ", scheduledAt=" + scheduledAt + ", retries=" + maxRetries + "}";
    }

    public static Builder builder(String from, String to, long amountCents) {
        return new Builder(from, to, amountCents);
    }

    public static final class Builder {
        private final String  from;
        private final String  to;
        private final long    amountCents;
        private String  currency       = "EUR";
        private String  reference;
        private String  idempotencyKey;
        private Instant scheduledAt;
        private int     maxRetries     = 3;

        private Builder(String from, String to, long amountCents) {
            if (from == null || to == null) throw new IllegalArgumentException("from/to required");
            if (amountCents <= 0)          throw new IllegalArgumentException("amount > 0");
            this.from        = from;
            this.to          = to;
            this.amountCents = amountCents;
        }

        public Builder currency(String c)       { this.currency       = c;  return this; }
        public Builder reference(String r)      { this.reference      = r;  return this; }
        public Builder idempotencyKey(String k) { this.idempotencyKey = k;  return this; }
        public Builder scheduledAt(Instant t)   { this.scheduledAt    = t;  return this; }
        public Builder maxRetries(int n)        { this.maxRetries     = n;  return this; }

        public Transfer build() { return new Transfer(this); }
    }
}
