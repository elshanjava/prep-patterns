package creational.factory;

public abstract class PaymentProcessor {

  // фабричный метод: ЧТО создавать, решает наследник
  protected abstract Payment createPayment();

  // шаблонный алгоритм поверх фабричного метода
  public final void handle() {
    Payment p = createPayment();
    p.process();
  }

}
