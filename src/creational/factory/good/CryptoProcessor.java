package creational.factory.good;

final class CryptoProcessor implements PaymentProcessor {
    @Override
    public void process(long amountCents) {
        System.out.println("[crypto] validating wallet address");
        System.out.println("[crypto] broadcasting transaction of " + amountCents);
        System.out.println("[crypto] waiting for 3 confirmations");
    }
}
