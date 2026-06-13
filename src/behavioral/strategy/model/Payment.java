package behavioral.strategy.model;

import java.math.BigDecimal;

public record Payment(PaymentMethod method, BigDecimal amount) {}
