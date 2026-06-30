package coding.behavioral.strategy;

import java.math.BigDecimal;

public class WeightShipping implements ShippingStrategy {
    private final BigDecimal ratePerKg;

    public WeightShipping(BigDecimal ratePerKg) {
        this.ratePerKg = ratePerKg;
    }

    @Override
    public BigDecimal cost(Order o) {
        return ratePerKg.multiply(o.weight());
    }
}
