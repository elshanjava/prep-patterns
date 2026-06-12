package creational.factory;

final class SepaProcessor extends PaymentProcessor {
    @Override protected Payment createPayment() { return new SepaPayment(); }
}
