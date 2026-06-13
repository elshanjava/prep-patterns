package behavioral.chainofresponsibility;

import java.util.List;

// Оркестратор цепочки: порядок задаётся снаружи (в Spring — List<FraudCheck> + @Order).
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
