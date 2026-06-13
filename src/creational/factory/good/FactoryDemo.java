package creational.factory.good;

public class FactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Factory Method [GOOD] — единый контракт, единый реестр ==");
        System.out.println();

        // Один и тот же вызов ProcessorFactory.create() используется везде:
        // PaymentService, FeeService, ValidationService — все получают нужный объект.
        for (var method : new String[]{"CARD", "SEPA", "CRYPTO", "APPLE_PAY"}) {
            PaymentProcessor p = ProcessorFactory.create(method);
            System.out.println("=== " + method + " ===");
            p.validate(100_00L);
            System.out.printf("  fee: %d cents%n", p.fee(100_00L));
            p.process(100_00L);
            System.out.println();
        }

        System.out.println("--- Неизвестный метод ---");
        try {
            ProcessorFactory.create("PAYPAL");
        } catch (IllegalArgumentException e) {
            System.out.println("  " + e.getMessage());
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  APPLE_PAY добавлен: 1 класс (ApplePayProcessor) + 1 строка в REGISTRY");
        System.out.println("  BadPaymentProcessor, BadFeeCalculator, BadPaymentValidator — не тронуты");
        System.out.println("  Компилятор проверяет: ApplePayProcessor обязан реализовать validate+fee+process");
        System.out.println("  Тест CardProcessor: изолирован, никакой SEPA-логики рядом");
    }
}
