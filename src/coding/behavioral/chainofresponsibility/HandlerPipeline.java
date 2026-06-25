package coding.behavioral.chainofresponsibility;

import java.util.List;
import java.util.Optional;

public class HandlerPipeline {
    private final List<HandlerCheck> handlerChecks;

    public HandlerPipeline(List<HandlerCheck> handlerChecks) {
        this.handlerChecks = handlerChecks;
    }

    public Decision process(HttpRequest request) {
        for (HandlerCheck handlerCheck : handlerChecks) {
            Optional<Decision> handle = handlerCheck.handle(request);
            if (handle.isPresent()) {
                return handle.get();
            }
        }
        return Decision.APPROVE;
    }
}
