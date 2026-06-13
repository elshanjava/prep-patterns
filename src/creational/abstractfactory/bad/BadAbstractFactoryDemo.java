package creational.abstractfactory.bad;

public class BadAbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Abstract Factory [BAD] — if/else допускает несовместимые комбинации ==");
        System.out.println();

        System.out.println("--- Stripe (корректно) ---");
        BadPspSetup.run("stripe", 1_500L, "{\"type\":\"payment_intent.succeeded\"}");

        System.out.println();
        System.out.println("--- Adyen (Adyen-клиент + Stripe-парсер — баг!) ---");
        BadPspSetup.run("adyen", 2_000L, "{\"eventCode\":\"AUTHORISATION\"}");

        System.out.println();
        System.out.println("--- Braintree (StripeClient + StripeParser — двойной баг!) ---");
        BadPspSetup.run("braintree", 3_000L, "{\"transaction\":{\"status\":\"settled\"}}");

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  Компилятор не видит несовместимость: все реализуют PspClient/WebhookParser");
        System.out.println("  Mисмэтч Adyen+StripeParser проявится только в runtime при первом вебхуке");
        System.out.println("  Добавить PayPal: найди все if/else по кодовой базе — где-то забудешь");
        System.out.println("  Тест: нельзя запустить Adyen-сценарий без риска что кто-то опять перепутает");
    }
}
