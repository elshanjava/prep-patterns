package behavioral.chainofresponsibility;

import java.util.Optional;
import java.util.Set;

final class VelocityCheck implements FraudCheck {
    private final Set<String> flaggedUsers;

    VelocityCheck(Set<String> flaggedUsers) { this.flaggedUsers = flaggedUsers; }

    public Optional<Decision> check(Payment p) {
        return flaggedUsers.contains(p.user())
                ? Optional.of(Decision.REVIEW)
                : Optional.empty();
    }
}
