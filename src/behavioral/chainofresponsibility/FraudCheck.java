package behavioral.chainofresponsibility;

import java.util.Optional;

interface FraudCheck {
    Optional<Decision> check(Payment p);
}
