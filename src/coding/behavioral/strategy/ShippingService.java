package coding.behavioral.strategy;

import java.math.BigDecimal;

public class ShippingService {
    private ShippingStrategy shippingStrategy;

    public ShippingService(ShippingStrategy shippingStrategy) {
        this.shippingStrategy = shippingStrategy;
    }

    public void setShippingStrategy(ShippingStrategy shippingStrategy) {
        this.shippingStrategy = shippingStrategy;
    }

    public BigDecimal calculate(Order order) {
        return shippingStrategy.cost(order);
    }
}
