package creational.factory;

final class CardProcessor extends PaymentProcessor {
    @Override protected Payment createPayment() {
      return new CardPayment();
    }
}
