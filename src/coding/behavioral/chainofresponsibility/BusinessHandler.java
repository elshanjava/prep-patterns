package coding.behavioral.chainofresponsibility;

import java.util.Optional;

// Крупная сумма — не отказ, а ручная проверка (REVIEW).
public class BusinessHandler implements HandlerCheck {
    private final long reviewThresholdCents;

    public BusinessHandler(long reviewThresholdCents) {
        this.reviewThresholdCents = reviewThresholdCents;
    }

    @Override
    public Optional<Decision> handle(HttpRequest request) {
        if (request.amountCents() > reviewThresholdCents) {
            return Optional.of(Decision.REVIEW);
        }
        return Optional.empty();
    }
}
