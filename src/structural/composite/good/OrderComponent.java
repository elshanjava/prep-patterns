package structural.composite.good;

import java.math.BigDecimal;

// Единый контракт для всех элементов дерева заказа.
// Добавить PromoItem: 1 новый класс, реализует все три метода — нигде else добавлять не нужно.
interface OrderComponent {
    String     name();
    BigDecimal total();
    BigDecimal discount();
    BigDecimal tax();
}
