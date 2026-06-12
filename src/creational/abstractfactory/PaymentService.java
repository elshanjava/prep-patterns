package creational.abstractfactory;

/**
 * Клиент паттерна. Получает ОДНУ фабрику -> все продукты гарантированно из
 * одного семейства (нельзя случайно скрестить Stripe-клиент с Adyen-парсером).
 *
 * PaymentService не знает, какой провайдер за интерфейсами стоит — он работает
 * только с PspClient и WebhookParser. Сменить провайдера = подменить одну
 * фабрику в конструкторе, остальной код не трогается.
 */
class PaymentService {
    private final PspClient client;        // продукт №1 семейства
    private final WebhookParser parser;    // продукт №2 того же семейства

    PaymentService(PspFactory f) {
        this.client = f.createClient();
        this.parser = f.createWebhookParser();  // невозможно смешать Stripe+Adyen
    }

    void pay(long amountCents) {
        client.charge(amountCents);
    }

    WebhookEvent onWebhook(String rawBody) {
        return parser.parse(rawBody);
    }
}
