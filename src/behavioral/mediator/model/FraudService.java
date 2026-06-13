package behavioral.mediator.model;

public final class FraudService {
    public void check(Order o) { System.out.println("fraud: checked order " + o.id() + " amount=" + o.amount()); }
}
