package coding.behavioral.chainofresponsibility;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Stateful-хэндлер: считает запросы на клиента, при превышении лимита — отказ.
public class RateLimitHandler implements HandlerCheck {
    private final int maxRequestsPerClient;
    private final Map<String, Integer> counts = new HashMap<>();

    public RateLimitHandler(int maxRequestsPerClient) {
        this.maxRequestsPerClient = maxRequestsPerClient;
    }

    @Override
    public Optional<Decision> handle(HttpRequest request) {
        int used = counts.merge(request.clientId(), 1, Integer::sum);
        if (used > maxRequestsPerClient) {
            return Optional.of(Decision.DECLINE);
        }
        return Optional.empty();
    }
}
