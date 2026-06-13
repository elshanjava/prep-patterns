package behavioral.strategy;

import java.math.BigDecimal;

final class SepaFee implements FeeStrategy {
    private static final BigDecimal FLAT = new BigDecimal("0.35");

    public PaymentMethod method() { return PaymentMethod.SEPA; }

    public BigDecimal fee(Payment p) { return FLAT; }
}
