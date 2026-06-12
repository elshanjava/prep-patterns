package creational.factory;

// Запуск: один и тот же шаблонный алгоритм handle(), разные продукты —
// какой именно Payment создать, решает конкретный наследник (фабричный метод).
public class FactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Factory Method ==");
        PaymentProcessor[] processors = { new CardProcessor(), new SepaProcessor() };
        for (PaymentProcessor p : processors) {
            p.handle();   // handle() общий, createPayment() переопределён
        }
    }
}
