package behavioral.mediator;

final class PaymentService {
    void charge(Order o) { System.out.println("payment: charged " + o.amount()); }
}
