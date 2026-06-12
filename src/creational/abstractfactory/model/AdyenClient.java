package creational.abstractfactory.model;

import creational.abstractfactory.PspClient;

public class AdyenClient implements PspClient {
  @Override
  public void charge(long amount) {
    System.out.println("[Adyen] charge " + amount + " cents");
  }
}
