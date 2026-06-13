package creational.abstractfactory.model;

public final class AdyenClient implements PspClient {
    public void charge(long amountCents) {
        System.out.println("  [Adyen] charge " + amountCents + " cents");
    }
}
