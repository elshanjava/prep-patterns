package creational.factory.good;

final class SepaProcessor implements PaymentProcessor {
    @Override
    public void process(long amountCents) {
        System.out.println("[sepa] validating IBAN checksum");
        System.out.println("[sepa] checking SEPA mandate");
        System.out.println("[sepa] initiating T+1 bank transfer of " + amountCents);
    }
}
