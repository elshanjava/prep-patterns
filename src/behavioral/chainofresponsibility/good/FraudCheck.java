package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.util.Optional;

interface FraudCheck {
    Optional<Decision> check(Payment p);
}
