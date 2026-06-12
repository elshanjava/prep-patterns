package structural.facade;

// Клиент фасада: знает один метод pay(), а не пять подсистем.
class PaymentController {
    private final PaymentFacade payments;
    PaymentController(PaymentFacade payments) { this.payments = payments; }

    PaymentResult pay(Order o) { return payments.pay(o); }
}
