package creational.factory.bad;

public class BadFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Factory Method [BAD] — один и тот же switch в трёх классах ==");
        System.out.println();

        var processor = new BadPaymentProcessor();
        var feeCalc   = new BadFeeCalculator();
        var validator = new BadPaymentValidator();

        for (var method : new String[]{"CARD", "SEPA", "CRYPTO"}) {
            System.out.println("=== " + method + " ===");
            validator.validate(method, 100_00L);
            System.out.printf("  fee: %d cents%n", feeCalc.calculate(method, 100_00L));
            processor.handle(method, 100_00L);
            System.out.println();
        }

        System.out.println("--- Добавляем APPLE_PAY (BadFeeCalculator забыли обновить) ---");
        try {
            validator.validate("APPLE_PAY", 50_00L);       // падает: ветки нет
            feeCalc.calculate("APPLE_PAY", 50_00L);
            processor.handle("APPLE_PAY", 50_00L);
        } catch (IllegalArgumentException e) {
            System.out.println("  runtime ошибка: " + e.getMessage());
            System.out.println("  компилятор молчал — интерфейса нет, контракт ничем не закреплён");
        }

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  Добавить APPLE_PAY = найти и отредактировать 3 файла:");
        System.out.println("    BadPaymentProcessor  → handle()");
        System.out.println("    BadFeeCalculator     → calculate()");
        System.out.println("    BadPaymentValidator  → validate()");
        System.out.println("  Пропустить один = баг в проде, платёж прошёл без комиссии или без валидации.");
    }
}
