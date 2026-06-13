package creational.factory.bad;

public class BadFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Factory Method [BAD] ==");
        var processor = new BadPaymentProcessor();

        System.out.println("--- CARD ---");
        processor.handle("CARD", 4999);

        System.out.println("--- SEPA ---");
        processor.handle("SEPA", 10000);

        // Добавить новый метод оплаты = правка BadPaymentProcessor везде по кодовой базе.
        // Нарушены OCP и SRP; логика выбора и логика обработки слиплись.
    }
}
