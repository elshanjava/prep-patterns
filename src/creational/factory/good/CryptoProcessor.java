package creational.factory.good;

final class CryptoProcessor implements PaymentProcessor {
    @Override
    public void validate(long amountCents) {
        System.out.println("[crypto] address format, dust threshold, AML screening");
        if (amountCents < 1_000L) throw new IllegalArgumentException("below dust threshold");
    }

    @Override
    public long fee(long amountCents) {
        return Math.round(amountCents * 0.010) + 500;  // network fee + custody spread
    }

    @Override
    public void process(long amountCents) {
        System.out.println("[crypto] broadcasting transaction of " + amountCents);
        System.out.println("[crypto] waiting for 3 confirmations");
    }
}
