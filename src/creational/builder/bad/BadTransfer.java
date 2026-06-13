package creational.builder.bad;

import java.time.Instant;
import java.util.Map;

// Телескопические конструкторы: 6 полей → 5 конструкторов.
// При вызове new BadTransfer("A","B",100_00,"USD","ref-1","key-42") очень легко
// перепутать reference и idempotencyKey — оба String, компилятор не заметит.
// Добавить scheduledAt? → ещё один конструктор, ещё больше путаницы.
final class BadTransfer {
    final String  from;
    final String  to;
    final long    amountCents;
    final String  currency;
    final String  reference;
    final String  idempotencyKey;
    final Instant scheduledAt;    // опциональное — добавили → 2 новых конструктора
    final int     maxRetries;     // опциональное — добавили → ещё 2 конструктора

    // Конструктор 1: только обязательные
    BadTransfer(String from, String to, long amountCents) {
        this(from, to, amountCents, "EUR", null, null, null, 3);
    }

    // Конструктор 2: с валютой
    BadTransfer(String from, String to, long amountCents, String currency) {
        this(from, to, amountCents, currency, null, null, null, 3);
    }

    // Конструктор 3: с reference
    BadTransfer(String from, String to, long amountCents, String currency, String reference) {
        this(from, to, amountCents, currency, reference, null, null, 3);
    }

    // Конструктор 4: с idempotencyKey — легко перепутать с reference (оба String!)
    BadTransfer(String from, String to, long amountCents,
                String currency, String reference, String idempotencyKey) {
        this(from, to, amountCents, currency, reference, idempotencyKey, null, 3);
    }

    // Конструктор 5: полный — 8 позиционных параметров. Угадай, что на 6-м месте.
    BadTransfer(String from, String to, long amountCents,
                String currency, String reference, String idempotencyKey,
                Instant scheduledAt, int maxRetries) {
        if (from == null || to == null) throw new IllegalArgumentException("from/to required");
        if (amountCents <= 0)          throw new IllegalArgumentException("amount > 0");
        this.from           = from;
        this.to             = to;
        this.amountCents    = amountCents;
        this.currency       = currency;
        this.reference      = reference;
        this.idempotencyKey = idempotencyKey;
        this.scheduledAt    = scheduledAt;
        this.maxRetries     = maxRetries;
    }

    @Override public String toString() {
        return "BadTransfer{from=" + from + ", to=" + to + ", amount=" + amountCents
               + " " + currency + ", ref=" + reference + ", key=" + idempotencyKey
               + ", scheduledAt=" + scheduledAt + ", retries=" + maxRetries + "}";
    }
}
