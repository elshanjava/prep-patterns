package creational.factory.good;

final class CardProcessor implements PaymentProcessor {
    @Override
    public void process(long amountCents) {
        System.out.println("[card] validating CVV and expiry");
        System.out.println("[card] running 3DS authentication");
        System.out.println("[card] charging " + amountCents + " via card network");
        System.out.println("[card] notifying issuer bank");
    }
}
