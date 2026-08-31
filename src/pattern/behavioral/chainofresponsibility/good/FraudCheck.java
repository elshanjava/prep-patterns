package pattern.behavioral.chainofresponsibility.good;

import pattern.behavioral.chainofresponsibility.model.Decision;
import pattern.behavioral.chainofresponsibility.model.Payment;

import java.util.Optional;

interface FraudCheck {
    Optional<Decision> check(Payment p);
}
