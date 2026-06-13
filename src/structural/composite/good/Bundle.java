package structural.composite.good;

import java.math.BigDecimal;
import java.util.List;

// Контейнер: делегирует все три метода дочерним элементам — без единого instanceof.
// Рекурсия на любую глубину, клиент её не видит.
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
        return sum(OrderComponent::total);
    }

    @Override
    public BigDecimal discount() {
        return sum(OrderComponent::discount);
    }

    @Override
    public BigDecimal tax() {
        return sum(OrderComponent::tax);
    }

    private BigDecimal sum(java.util.function.Function<OrderComponent, BigDecimal> fn) {
        return children.stream().map(fn).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
