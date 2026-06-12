package creational.factory;

public class CardPayment implements Payment {
  @Override
  public void process() {
    System.out.println("CardPayment");
  }
}
