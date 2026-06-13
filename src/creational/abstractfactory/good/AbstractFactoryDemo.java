package creational.abstractfactory.good;

import java.util.List;

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Abstract Factory [GOOD] — фабрика гарантирует согласованность семейства ==");
        System.out.println();

        // Все три провайдера используют один и тот же PaymentService — ни одной строки if/else
        var providers = List.of(
                new PaymentService(new StripeFactory()),
                new PaymentService(new AdyenFactory()),
                new PaymentService(new BraintreeFactory())   // добавлен: 1 класс BraintreeFactory
        );

        var webhooks = List.of(
                "{\"type\":\"payment_intent.succeeded\"}",
                "{\"eventCode\":\"AUTHORISATION\"}",
                "{\"transaction\":{\"status\":\"settled\"}}"
        );

        var names = List.of("Stripe", "Adyen", "Braintree");

        for (int i = 0; i < providers.size(); i++) {
            System.out.println("--- " + names.get(i) + " ---");
            providers.get(i).charge(5_000L);
            var event = providers.get(i).handleWebhook(webhooks.get(i));
            System.out.println("  event: " + event.type());
            System.out.println();
        }

        System.out.println("Преимущества над bad:");
        System.out.println("  Adyen + AdyenParser гарантированы фабрикой — мисмэтч невозможен");
        System.out.println("  Добавить Braintree: 1 файл BraintreeFactory, 1 строка в demo — всё");
        System.out.println("  Переключить провайдера: new PaymentService(new AdyenFactory()) — 1 строка");
        System.out.println("  Тест: new PaymentService(new MockPspFactory()) — полная изоляция");
    }
}
