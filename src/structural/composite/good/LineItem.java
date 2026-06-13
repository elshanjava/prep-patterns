package structural.composite.good;

import java.math.BigDecimal;

record LineItem(String name, BigDecimal price) implements OrderComponent {
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.05");
    private static final BigDecimal VAT_RATE      = new BigDecimal("0.20");

    @Override public BigDecimal total()    { return price; }
    @Override public BigDecimal discount() { return price.multiply(DISCOUNT_RATE); }
    @Override public BigDecimal tax()      { return price.multiply(VAT_RATE); }
}
