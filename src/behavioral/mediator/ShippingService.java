package behavioral.mediator;

final class ShippingService {
    void schedule(Order o) { System.out.println("shipping: scheduled " + o.id()); }
}
