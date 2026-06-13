package structural.composite.good;

import java.math.BigDecimal;

// Лист дерева: не содержит дочерних элементов.
record LineItem(String name, BigDecimal price) implements OrderComponent {
    @Override public BigDecimal total() { return price; }
}
