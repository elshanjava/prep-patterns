package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.Optional;

final class LimitCheck implements FraudCheck {
    private static final BigDecimal LIMIT = new BigDecimal("5000");

    public Optional<Decision> check(Payment p) {
        return p.amount().compareTo(LIMIT) > 0
                ? Optional.of(Decision.DECLINE)
                : Optional.empty();
    }
}
