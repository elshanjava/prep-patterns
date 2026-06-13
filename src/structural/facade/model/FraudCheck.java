package structural.facade.model;

public final class FraudCheck {
    public void validate(Order order) {
        System.out.println("  [fraud] ok for " + order.customer());
    }
}
