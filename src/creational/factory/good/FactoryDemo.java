package creational.factory.good;

public class FactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Factory Method [GOOD] — фабрика скрывает выбор реализации ==");

        for (var method : new String[]{"CARD", "SEPA", "CRYPTO"}) {
            System.out.println("\n[" + method + "]");
            PaymentProcessor processor = ProcessorFactory.create(method);
            processor.process(10_000L);
        }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - клиент работает с PaymentProcessor, не знает CardProcessor/SepaProcessor");
        System.out.println("  - добавить Apple Pay: ApplePayProcessor + 1 строка в ProcessorFactory (OCP)");
        System.out.println("  - тест CardProcessor изолирован от SEPA-логики");
        System.out.println("  - в Spring: List<PaymentProcessor> processors — DI сам собирает все реализации");
    }
}
