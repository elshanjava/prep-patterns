package creational.abstractfactory.model;

public final class BraintreeClient implements PspClient {
    public void charge(long amountCents) {
        System.out.println("  [Braintree] charge " + amountCents + " cents via SDK");
    }
}
