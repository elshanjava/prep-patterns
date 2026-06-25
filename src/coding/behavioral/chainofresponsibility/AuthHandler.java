package coding.behavioral.chainofresponsibility;

import java.util.Optional;

// Нет токена — сразу отказ, дальше по цепочке не идём.
public class AuthHandler implements HandlerCheck {

    @Override
    public Optional<Decision> handle(HttpRequest request) {
        if (request.authToken() == null || request.authToken().isBlank()) {
            return Optional.of(Decision.DECLINE);
        }
        return Optional.empty();
    }
}
