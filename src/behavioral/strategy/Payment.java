package behavioral.strategy;

import java.math.BigDecimal;

record Payment(PaymentMethod method, BigDecimal amount) {}
