package behavioral.strategy.good;

import behavioral.strategy.model.Payment;
import behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// В Spring конструктор получает List<FeeStrategy> из DI-контейнера автоматически.
final class FeeCalculator {
    private final Map<PaymentMethod, FeeStrategy> strategies;

    FeeCalculator(List<FeeStrategy> list) {
        this.strategies = list.stream()
                .collect(Collectors.toMap(FeeStrategy::method, Function.identity()));
    }

    public BigDecimal fee(Payment p) {
        var strategy = strategies.get(p.method());
        if (strategy == null) throw new UnsupportedOperationException(p.method().name());
        return strategy.fee(p);
    }
}
