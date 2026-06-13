package behavioral.mediator.model;

public final class ShippingService {
    public void schedule(Order o) { System.out.println("shipping: scheduled " + o.id()); }
}
