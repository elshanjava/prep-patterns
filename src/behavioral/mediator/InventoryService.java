package behavioral.mediator;

final class InventoryService {
    void reserve(Order o) { System.out.println("inventory: reserved " + o.id()); }
}
