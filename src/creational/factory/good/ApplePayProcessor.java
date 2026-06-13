package creational.factory.good;

// Добавление APPLE_PAY: один новый файл — больше ничего менять не нужно.
// OCP: ProcessorFactory открыта для расширения, закрыта для изменения.
final class ApplePayProcessor implements PaymentProcessor {
    @Override
    public void validate(long amountCents) {
        System.out.println("[apple-pay] device token validation, Face ID confirmation");
        if (amountCents > 300_000_00L) throw new IllegalArgumentException("exceeds Apple Pay limit");
    }

    @Override
    public long fee(long amountCents) {
        return Math.round(amountCents * 0.0150);   // Apple keeps 0.15% from merchant
    }

    @Override
    public void process(long amountCents) {
        System.out.println("[apple-pay] secure element authorization");
        System.out.println("[apple-pay] token exchange with payment network");
        System.out.println("[apple-pay] charging " + amountCents + " via card-on-file");
    }
}
