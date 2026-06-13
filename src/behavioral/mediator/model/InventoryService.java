package behavioral.mediator.model;

public final class InventoryService {
    public void reserve(Order o) { System.out.println("inventory: reserved " + o.id()); }
}
