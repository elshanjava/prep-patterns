package creational.builder.good;

import java.time.Instant;

public class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Builder [GOOD] — именованные поля, компилятор ловит опечатки ==");
        System.out.println();

        // Только обязательные — чисто и понятно
        var simple = Transfer.builder("ACC-A", "ACC-B", 100_00L)
                .build();
        System.out.println("simple:    " + simple);

        // С currency — понятно что значит каждое поле
        var usd = Transfer.builder("ACC-A", "ACC-B", 100_00L)
                .currency("USD")
                .build();
        System.out.println("usd:       " + usd);

        // Полный — каждое поле читается как предложение
        var full = Transfer.builder("ACC-A", "ACC-B", 50_00L)
                .currency("GBP")
                .reference("INV-2024-999")
                .idempotencyKey("key-xyz")          // ← невозможно перепутать с reference
                .scheduledAt(Instant.parse("2024-12-31T23:59:59Z"))
                .maxRetries(5)
                .build();
        System.out.println("full:      " + full);

        // Retry-сценарий: один и тот же idempotencyKey — платёж не задублируется
        var retry = Transfer.builder("ACC-A", "ACC-B", 50_00L)
                .currency("GBP")
                .idempotencyKey("key-xyz")
                .build();
        System.out.println("retry:     " + retry);
        System.out.println("same key?  " + full.idempotencyKey().equals(retry.idempotencyKey()));

        // Валидация падает при build(), не где-то внутри бизнес-логики
        try {
            Transfer.builder("ACC-A", null, 100_00L).build();
        } catch (IllegalArgumentException e) {
            System.out.println("invalid:   " + e.getMessage());
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  .idempotencyKey(\"k\") vs .reference(\"r\") — перепутать невозможно, оба String");
        System.out.println("  Добавить поле metadata: 1 метод в Builder, 0 изменений в существующих вызовах");
        System.out.println("  null-ы не нужны: не вызвал — поле остаётся default'ом");
        System.out.println("  Объект иммутабельный после build() — можно безопасно кэшировать и шарить");
    }
}
