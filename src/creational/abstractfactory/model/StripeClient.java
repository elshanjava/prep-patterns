package creational.abstractfactory.model;

public final class StripeClient implements PspClient {
    public void charge(long amountCents) {
        System.out.println("  [Stripe] charge " + amountCents + " cents");
    }
}
