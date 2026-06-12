package structural.composite;

import java.math.BigDecimal;

final class LineItem implements OrderComponent {
    private final BigDecimal price;
    LineItem(BigDecimal price) { this.price = price; }
    public BigDecimal total() { return price; }
}
