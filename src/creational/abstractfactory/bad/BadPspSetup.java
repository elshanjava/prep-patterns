package creational.abstractfactory.bad;

import creational.abstractfactory.model.*;

// if/else на строке провайдера вместо фабрики.
// Можно случайно скрестить StripeClient с AdyenWebhookParser — компилятор не заметит.
// Добавить Braintree? — найди все такие if/else по кодовой базе и добавь ветку.
final class BadPspSetup {

    static void run(String provider, long amountCents, String rawWebhook) {
        PspClient client;
        WebhookParser parser;

        if ("stripe".equals(provider)) {
            client = new StripeClient();
            parser = new StripeWebhookParser();

        } else if ("adyen".equals(provider)) {
            client = new AdyenClient();
            // — вот тут инженер в пятницу скопипастил не ту строчку:
            parser = new StripeWebhookParser();  // ← Adyen-клиент + Stripe-парсер!
            // Компилятор молчит: оба реализуют WebhookParser.
            // Баг проявится только в runtime при первом вебхуке от Adyen.

        } else {
            throw new IllegalArgumentException("unknown provider: " + provider);
        }

        client.charge(amountCents);
        WebhookEvent event = parser.parse(rawWebhook);
        System.out.println("  parsed: " + event.type());
    }
}
