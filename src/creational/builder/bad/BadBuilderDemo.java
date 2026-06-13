package creational.builder.bad;

public class BadBuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Builder [BAD] ==");

        // Какой null? Это reference или idempotencyKey?
        // IDE покажет только позицию аргумента — придётся смотреть сигнатуру.
        var t1 = new BadTransfer("ACC-A", "ACC-B", 100, "EUR", null, "key-001");
        System.out.println("created: " + t1);

        // Распространённая ошибка: перепутали порядок — "key-001" попало в reference
        var bug = new BadTransfer("ACC-A", "ACC-B", 100, "EUR", "key-001", null);
        System.out.println("bug (key in wrong slot): " + bug);

        // Добавить 6-е поле (например, description)?
        // → ещё один конструктор, правь все существующие вызовы по всему коду.
    }
}
