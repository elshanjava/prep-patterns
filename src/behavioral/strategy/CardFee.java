package behavioral.strategy;

import java.math.BigDecimal;

final class CardFee implements FeeStrategy {
    private static final BigDecimal RATE  = new BigDecimal("0.029");
    private static final BigDecimal FIXED = new BigDecimal("0.30");

    public PaymentMethod method() { return PaymentMethod.CARD; }

    public BigDecimal fee(Payment p) {
        return p.amount().multiply(RATE).add(FIXED);
    }
}
