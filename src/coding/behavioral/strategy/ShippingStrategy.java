package coding.behavioral.strategy;

import java.math.BigDecimal;

public interface ShippingStrategy {
    BigDecimal cost(Order o);
}
