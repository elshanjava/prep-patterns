package structural.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

final class Bundle implements OrderComponent {
    private final List<OrderComponent> children = new ArrayList<>();
    public void add(OrderComponent c) { children.add(c); }
    public BigDecimal total() {                  // рекурсия инкапсулирована здесь
        return children.stream()
                       .map(OrderComponent::total)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
