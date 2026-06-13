package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.util.List;

// Порядок и состав задаются снаружи (в Spring — List<FraudCheck> + @Order).
final class FraudPipeline {
    private final List<FraudCheck> checks;

    FraudPipeline(List<FraudCheck> checks) { this.checks = checks; }

    public Decision evaluate(Payment p) {
        for (FraudCheck check : checks) {
            var decision = check.check(p);
            if (decision.isPresent()) return decision.get();
        }
        return Decision.APPROVE;
    }
}
