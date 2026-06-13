package structural.composite.good;

import java.math.BigDecimal;
import java.util.List;

// Контейнер: делегирует total() дочерним элементам — рекурсия без instanceof.
// Может содержать как LineItem, так и другие Bundle (произвольная вложенность).
final class Bundle implements OrderComponent {
    private final String               name;
    private final List<OrderComponent> children;

    Bundle(String name, List<OrderComponent> children) {
        this.name     = name;
        this.children = List.copyOf(children);
    }

    @Override public String name() { return name; }

    @Override
    public BigDecimal total() {
        return children.stream()
                       .map(OrderComponent::total)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
