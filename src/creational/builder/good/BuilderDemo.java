package creational.builder.good;

public class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Builder [GOOD] — читаемое создание сложного объекта ==");

        // Только обязательные поля — читаешь и сразу понимаешь что к чему
        Transfer simple = Transfer.builder("acc-A", "acc-B", 50_00L)
                .build();
        System.out.println("simple:  " + simple);

        // Опциональные поля — каждое именованное, порядок не важен
        Transfer full = Transfer.builder("acc-A", "acc-B", 100_00L)
                .currency("USD")
                .reference("Invoice-2024-001")
                .idempotencyKey("key-xyz-42")
                .build();
        System.out.println("full:    " + full);

        // Валидация при build() — не где-то в бизнес-логике
        try {
            Transfer.builder(null, "acc-B", 100_00L).build();
        } catch (IllegalArgumentException e) {
            System.out.println("invalid: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - .currency(\"USD\").reference(\"inv-1\") читается как предложение");
        System.out.println("  - нет позиционной путаницы: new BadTransfer(\"A\",\"B\",100,null,\"key\",null)");
        System.out.println("  - опциональные поля не требуют null — просто не вызываешь метод");
        System.out.println("  - объект иммутабельный после build()");
    }
}
