package coding.behavioral.strategy;

import java.math.BigDecimal;

public class FreeOverShipping implements ShippingStrategy{
    private final BigDecimal threshold;
    private final BigDecimal otherwise;

    public FreeOverShipping(BigDecimal threshold, BigDecimal otherwise) {
        this.threshold = threshold;
        this.otherwise = otherwise;
    }

    @Override
    public BigDecimal cost(Order o) {
        return o.total().compareTo(threshold) > 0 ? BigDecimal.ZERO : otherwise;
    }
}
