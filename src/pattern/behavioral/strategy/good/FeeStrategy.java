package pattern.behavioral.strategy.good;

import pattern.behavioral.strategy.model.Payment;
import pattern.behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;

interface FeeStrategy {
    PaymentMethod method();
    BigDecimal fee(Payment p);
}
