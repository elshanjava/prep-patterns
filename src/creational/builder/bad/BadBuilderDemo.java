package creational.builder.bad;

import java.time.Instant;

public class BadBuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Builder [BAD] — 8 позиционных параметров, компилятор не поможет ==");
        System.out.println();

        // Минимальный вариант — выглядит нормально
        var simple = new BadTransfer("ACC-A", "ACC-B", 100_00L);
        System.out.println("simple:   " + simple);

        // С null-ами: какой из них reference, а какой idempotencyKey?
        var t1 = new BadTransfer("ACC-A", "ACC-B", 100_00L, "USD", null, "key-001");
        System.out.println("t1:       " + t1);

        // Классический баг: перепутали 5-й и 6-й аргумент (оба String)
        // reference = "key-001", idempotencyKey = null
        // Идемпотентный ключ пустой → дублирующий платёж при retry!
        var bug = new BadTransfer("ACC-A", "ACC-B", 100_00L, "USD", "key-001", null);
        System.out.println("bug:      " + bug);
        System.out.println("  ↑ key-001 попал в reference вместо idempotencyKey — дубли при retry!");

        // Полный конструктор: 8 аргументов подряд — угадай что на 7-м месте
        var full = new BadTransfer("ACC-A", "ACC-B", 50_00L,
                "GBP", "INV-2024-999", "key-xyz",
                Instant.parse("2024-12-31T23:59:59Z"), 5);
        System.out.println("full:     " + full);

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  8 аргументов подряд → позиционная путаница, IDE не спасает");
        System.out.println("  Оба 5-й и 6-й аргумент String → компилятор не поймает перестановку");
        System.out.println("  Добавить 9-е поле → новый конструктор + правь ВСЕ вызовы по кодовой базе");
        System.out.println("  null-ы как заглушки опциональных полей → не понятно что пропущено");
    }
}
