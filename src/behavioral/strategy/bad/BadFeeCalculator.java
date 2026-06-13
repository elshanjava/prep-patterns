package behavioral.strategy.bad;

import behavioral.strategy.model.Payment;

import java.math.BigDecimal;

// Все алгоритмы зашиты в один switch.
// Новый способ оплаты = правка этого метода; нельзя тестировать алгоритм отдельно.
final class BadFeeCalculator {
    BigDecimal fee(Payment p) {
        return switch (p.method()) {
            case CARD   -> p.amount().multiply(new BigDecimal("0.029")).add(new BigDecimal("0.30"));
            case SEPA   -> new BigDecimal("0.35");
            case CRYPTO -> p.amount().multiply(new BigDecimal("0.015"));
        };
    }
}
