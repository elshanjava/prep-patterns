package creational.factory.good;

final class SepaProcessor implements PaymentProcessor {
    @Override
    public void validate(long amountCents) {
        System.out.println("[sepa] IBAN checksum, BIC reachability, mandate status");
        if (amountCents > 100_000_00L) throw new IllegalArgumentException("exceeds SEPA CT limit");
    }

    @Override
    public long fee(long amountCents) {
        return Math.max(30L, Math.round(amountCents * 0.003));  // min €0.30
    }

    @Override
    public void process(long amountCents) {
        System.out.println("[sepa] checking SEPA mandate");
        System.out.println("[sepa] initiating T+1 bank transfer of " + amountCents);
    }
}
