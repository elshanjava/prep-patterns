package creational.abstractfactory.bad;

import creational.abstractfactory.model.*;

// if/else вместо фабрики: компилятор не мешает перепутать провайдеров.
// Брайнтри добавлен — но кто-то скопипастил не ту строчку в пятницу.
// Добавить 4-й провайдер: найди все if/else по кодовой базе и добавь ветку.
final class BadPspSetup {

    static void run(String provider, long amountCents, String rawWebhook) {
        PspClient     client;
        WebhookParser parser;

        if ("stripe".equals(provider)) {
            client = new StripeClient();
            parser = new StripeWebhookParser();

        } else if ("adyen".equals(provider)) {
            client = new AdyenClient();
            parser = new StripeWebhookParser();     // ← Adyen + Stripe-парсер! (пятничный копипаст)
            // Компилятор молчит: оба реализуют WebhookParser.
            // Баг проявится только при первом вебхуке от Adyen в проде.

        } else if ("braintree".equals(provider)) {
            client = new StripeClient();            // ← тоже ошибка: StripeClient для Braintree!
            parser = new StripeWebhookParser();     // ← и парсер тоже Stripe
            // Два мисмэтча в одном блоке — добавляли второпях к дедлайну.

        } else {
            throw new IllegalArgumentException("unknown provider: " + provider);
        }

        client.charge(amountCents);
        WebhookEvent event = parser.parse(rawWebhook);
        System.out.println("  event type: " + event.type()
                           + (event.type().startsWith("stripe") && provider.equals("adyen")
                              ? "  ← WRONG! Adyen body parsed with Stripe parser" : ""));
    }
}
