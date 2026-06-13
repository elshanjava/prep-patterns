package behavioral.strategy;

import java.math.BigDecimal;

interface FeeStrategy {
    PaymentMethod method();
    BigDecimal fee(Payment p);
}
