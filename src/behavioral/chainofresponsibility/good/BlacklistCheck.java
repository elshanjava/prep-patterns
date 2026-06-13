package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

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
