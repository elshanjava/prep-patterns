package creational.builder.good;

import java.time.Instant;

// Staged (Step) Builder: обязательные поля — отдельные шаги, каждый именован.
// Компилятор заставляет пройти from → to → amountCents по очереди, .build() недоступен раньше.
// Решает обе проблемы сразу: нельзя забыть обязательное поле И нельзя перепутать from/to местами.
public final class Transfer {
    private final String  from;
    private final String  to;
    private final long    amountCents;
    private final String  currency;
    private final String  reference;
    private final String  idempotencyKey;
    private final Instant scheduledAt;
    private final int     maxRetries;

    // Инвариант защищает сам объект: даже в обход билдера невалидный Transfer не родится.
    private Transfer(Builder b) {
        if (b.from == null || b.to == null) throw new IllegalArgumentException("from/to required");
        if (b.amountCents <= 0)             throw new IllegalArgumentException("amount > 0");
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

    // Точка входа возвращает первый шаг — только метод from() доступен.
    public static FromStep builder() {
        return new Builder();
    }

    // ── Ступени: каждый интерфейс открывает доступ только к следующему обязательному полю ──
    public interface FromStep   {
        ToStep from(String from); }
    public interface ToStep     {
        AmountStep to(String to); }
    public interface AmountStep {
        OptionalStep amountCents(long amountCents); }

    // Последний шаг: опциональные поля + build(). До него нельзя дойти, не пройдя обязательные.
    public interface OptionalStep {
        OptionalStep currency(String currency);
        OptionalStep reference(String reference);
        OptionalStep idempotencyKey(String idempotencyKey);
        OptionalStep scheduledAt(Instant scheduledAt);
        OptionalStep maxRetries(int maxRetries);
        Transfer build();
    }

    // Один класс реализует все ступени — поля накапливаются, типы возврата ведут по шагам.
    private static final class Builder implements FromStep, ToStep, AmountStep, OptionalStep {
        private String  from;
        private String  to;
        private long    amountCents;
        private String  currency       = "EUR";
        private String  reference;
        private String  idempotencyKey;
        private Instant scheduledAt;
        private int     maxRetries     = 3;

        @Override public ToStep       from(String from)         { this.from = from;               return this; }
        @Override public AmountStep   to(String to)             { this.to = to;                   return this; }
        @Override public OptionalStep amountCents(long amount)  { this.amountCents = amount;      return this; }

        @Override public OptionalStep currency(String c)        { this.currency = c;              return this; }
        @Override public OptionalStep reference(String r)       { this.reference = r;             return this; }
        @Override public OptionalStep idempotencyKey(String k)  { this.idempotencyKey = k;        return this; }
        @Override public OptionalStep scheduledAt(Instant t)    { this.scheduledAt = t;           return this; }
        @Override public OptionalStep maxRetries(int n)         { this.maxRetries = n;            return this; }

        @Override public Transfer build() { return new Transfer(this); }
    }
}
