package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.util.Optional;
import java.util.Set;

// Новое правило = новый файл. FraudPipeline и остальные звенья не трогаются.
// В Spring: @Component @Order(3) — порядок регулируется аннотацией, не правкой кода.
final class GeoCheck implements FraudCheck {
    private final Set<String> blockedCountries;

    GeoCheck(Set<String> blockedCountries) { this.blockedCountries = blockedCountries; }

    public Optional<Decision> check(Payment p) {
        return blockedCountries.contains(p.country())
                ? Optional.of(Decision.DECLINE)
                : Optional.empty();
    }
}
