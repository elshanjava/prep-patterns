package creational.factory.good;

final class CardProcessor implements PaymentProcessor {
    @Override
    public void validate(long amountCents) {
        System.out.println("[card] checking BIN list, expiry window, CVV presence");
        if (amountCents > 500_000_00L) throw new IllegalArgumentException("exceeds card single-txn limit");
    }

    @Override
    public long fee(long amountCents) {
        return Math.round(amountCents * 0.0280);   // interchange 1.5% + scheme 0.5% + markup 0.8%
    }

    @Override
    public void process(long amountCents) {
        System.out.println("[card] running 3DS authentication");
        System.out.println("[card] charging " + amountCents + " via card network");
        System.out.println("[card] notifying issuer bank");
    }
}
