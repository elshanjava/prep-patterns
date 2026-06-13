package behavioral.chainofresponsibility.bad;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.Set;

// Все fraud-проверки слились в один метод.
// Нельзя переставить порядок, отключить проверку по конфигу,
// переиспользовать звено или тестировать его изолированно.
final class FraudService {
    private static final BigDecimal LIMIT = new BigDecimal("5000");
    private final Set<String> blacklist;
    private final Set<String> flaggedUsers;

    FraudService(Set<String> blacklist, Set<String> flaggedUsers) {
        this.blacklist = blacklist;
        this.flaggedUsers = flaggedUsers;
    }

    public Decision check(Payment p) {
        if (p.amount().compareTo(LIMIT) > 0)  return Decision.DECLINE;  // хочешь переставить?
        if (blacklist.contains(p.card()))      return Decision.DECLINE;  // правь этот метод
        if (flaggedUsers.contains(p.user()))   return Decision.REVIEW;   // добавить GEO?
        return Decision.APPROVE;                                         // правь этот метод
    }
}
