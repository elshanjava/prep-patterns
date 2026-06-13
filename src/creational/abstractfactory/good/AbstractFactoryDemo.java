package creational.abstractfactory.good;

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Abstract Factory [GOOD] — фабрика гарантирует согласованность семейства ==");

        System.out.println("\n--- Stripe ---");
        var stripe = new PaymentService(new StripeFactory());
        stripe.charge(5_000L);
        var stripeEvent = stripe.handleWebhook("{\"type\":\"payment_intent.succeeded\"}");
        System.out.println("  parsed: " + stripeEvent.type());

        System.out.println("\n--- Adyen ---");
        var adyen = new PaymentService(new AdyenFactory());
        adyen.charge(7_500L);
        var adyenEvent = adyen.handleWebhook("{\"eventCode\":\"AUTHORISATION\"}");
        System.out.println("  parsed: " + adyenEvent.type());

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - невозможно скрестить StripeClient с AdyenWebhookParser — фабрика не даст");
        System.out.println("  - переключить провайдера: new PaymentService(new AdyenFactory())");
        System.out.println("  - добавить Braintree: BraintreeFactory + 2 класса, 0 правок в PaymentService");
        System.out.println("  - в тестах: new PaymentService(new MockPspFactory()) — полная изоляция");
    }
}
