package coding.behavioral.chainofresponsibility;

import java.util.Optional;

// Битый запрос (нет clientId или сумма <= 0) — отказ.
public class ValidationHandler implements HandlerCheck {

    @Override
    public Optional<Decision> handle(HttpRequest request) {
        if (request.clientId() == null || request.clientId().isBlank()) {
            return Optional.of(Decision.DECLINE);
        }
        if (request.amountCents() <= 0) {
            return Optional.of(Decision.DECLINE);
        }
        return Optional.empty();
    }
}
