package pattern.creational.abstractfactory.good;

import pattern.creational.abstractfactory.model.PspClient;
import pattern.creational.abstractfactory.model.WebhookEvent;
import pattern.creational.abstractfactory.model.WebhookParser;

// Клиент работает только с PspFactory — не знает о Stripe или Adyen.
// Переключение провайдера: new PaymentService(new AdyenFactory()) — одна строка.
final class PaymentService {
    private final PspClient     client;
    private final WebhookParser parser;

    PaymentService(PspFactory factory) {
        this.client = factory.createClient();
        this.parser = factory.createParser();
    }

    void charge(long amountCents) {
        client.charge(amountCents);
    }

    WebhookEvent handleWebhook(String raw) {
        return parser.parse(raw);
    }
}
