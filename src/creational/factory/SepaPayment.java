package creational.factory;

public class SepaPayment implements Payment {
  @Override
  public void process() {
    System.out.println("SepaPayment");
  }
}
