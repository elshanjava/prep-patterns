package behavioral.mediator.model;

public final class PaymentService {
    public void charge(Order o) { System.out.println("payment: charged " + o.amount()); }
}
