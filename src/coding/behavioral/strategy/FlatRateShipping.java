package coding.behavioral.strategy;

import java.math.BigDecimal;

public class FlatRateShipping implements ShippingStrategy {
    private final BigDecimal price;

    public FlatRateShipping(BigDecimal price) {
        this.price = price;
    }

    @Override
    public BigDecimal cost(Order o) {
        return price;
    }


}
