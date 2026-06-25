package coding.behavioral.chainofresponsibility;

import java.util.Optional;

public interface HandlerCheck {
    Optional<Decision> handle(HttpRequest request);
}
