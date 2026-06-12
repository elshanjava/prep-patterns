package creational.abstractfactory;

// Запуск: показывает, что выбор фабрики определяет ВСЁ семейство продуктов.
public class AbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Abstract Factory ==");
        run(new StripeFactory());   // семейство Stripe
        run(new AdyenFactory());    // семейство Adyen
    }

    private static void run(PspFactory factory) {
        PaymentService service = new PaymentService(factory);
        service.pay(1500);
        WebhookEvent event = service.onWebhook("{...raw body...}");
        System.out.println("  parsed event: " + event.type());
    }
}
