package creational.abstractfactory.bad;

public class BadAbstractFactoryDemo {
    public static void main(String[] args) {
        System.out.println("== Abstract Factory [BAD] ==");

        System.out.println("--- Stripe (ok) ---");
        BadPspSetup.run("stripe", 1500, "{...stripe body...}");

        System.out.println("--- Adyen (mixed provider bug!) ---");
        BadPspSetup.run("adyen", 2000, "{...adyen body...}");
        // Adyen-клиент зарядил правильно, но вебхук распарсен Stripe-парсером.
        // event.type() вернёт "stripe.payment_intent.succeeded" для Adyen-тела.
    }
}
