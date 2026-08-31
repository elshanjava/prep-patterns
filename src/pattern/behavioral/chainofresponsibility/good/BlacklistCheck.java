package pattern.behavioral.chainofresponsibility.good;

import pattern.behavioral.chainofresponsibility.model.Decision;
import pattern.behavioral.chainofresponsibility.model.Payment;

import java.util.Optional;
import java.util.Set;

final class BlacklistCheck implements FraudCheck {
    private final Set<String> blacklist;

    BlacklistCheck(Set<String> blacklist) { this.blacklist = blacklist; }

    public Optional<Decision> check(Payment p) {
        return blacklist.contains(p.card())
                ? Optional.of(Decision.DECLINE)
                : Optional.empty();
    }
}
