package behavioral.strategy.good;

import behavioral.strategy.model.Payment;
import behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;

interface FeeStrategy {
    PaymentMethod method();
    BigDecimal fee(Payment p);
}
