package creational.builder.good;

import java.time.Instant;

public class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Builder [GOOD] — staged builder: обязательные поля как шаги, всё именовано ==");
        System.out.println();

        // Обязательные поля — отдельные именованные шаги: from → to → amountCents.
        // Перепутать from/to местами невозможно — каждый назван, а не позиционный аргумент.
        var simple = Transfer.builder()
                .from("ACC-A")
                .to("ACC-B")
                .amountCents(100_00L)
                .build();
        System.out.println("simple:    " + simple);

        // С currency — опциональные поля идут после обязательных
        var usd = Transfer.builder()
                .from("ACC-A")
                .to("ACC-B")
                .amountCents(100_00L)
                .currency("USD")
                .build();
        System.out.println("usd:       " + usd);

        // Полный — каждое поле читается как предложение
        var full = Transfer.builder()
                .from("ACC-A")
                .to("ACC-B")
                .amountCents(50_00L)
                .currency("GBP")
                .reference("INV-2024-999")
                .idempotencyKey("key-xyz")          // ← невозможно перепутать с reference
                .scheduledAt(Instant.parse("2024-12-31T23:59:59Z"))
                .maxRetries(5)
                .build();
        System.out.println("full:      " + full);

        // Retry-сценарий: один и тот же idempotencyKey — платёж не задублируется
        var retry = Transfer.builder()
                .from("ACC-A")
                .to("ACC-B")
                .amountCents(50_00L)
                .currency("GBP")
                .idempotencyKey("key-xyz")
                .build();
        System.out.println("retry:     " + retry);
        System.out.println("same key?  " + full.idempotencyKey().equals(retry.idempotencyKey()));

        // Валидация падает при build() — инвариант защищён конструктором Transfer
        try {
            Transfer.builder().from("ACC-A").to(null).amountCents(100_00L).build();
        } catch (IllegalArgumentException e) {
            System.out.println("invalid:   " + e.getMessage());
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  from()/to() — отдельные шаги: перепутать местами невозможно (оба String)");
        System.out.println("  Компилятор не даст вызвать build(), пока не пройдены from → to → amountCents");
        System.out.println("  .idempotencyKey(\"k\") vs .reference(\"r\") — перепутать невозможно, оба String");
        System.out.println("  Валидация в конструкторе Transfer — объект всегда валиден, даже в обход билдера");
        System.out.println("  Объект иммутабельный после build() — можно безопасно кэшировать и шарить");
    }
}
