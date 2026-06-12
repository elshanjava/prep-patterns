package creational.abstractfactory.model;

import creational.abstractfactory.PspClient;

public class StripeClient implements PspClient {
  @Override
  public void charge(long amount) {
    System.out.println("[Stripe] charge " + amount + " cents");
  }
}
