package structural.facade;

// Подсистема №1: антифрод. Часть «сложной» начинки, которую прячет фасад.
final class FraudCheck {
    void validate(Order order) {
        System.out.println("  [fraud] ok for " + order.customer());
    }
}
