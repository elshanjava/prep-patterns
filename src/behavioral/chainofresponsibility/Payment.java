package behavioral.chainofresponsibility;

import java.math.BigDecimal;

record Payment(String card, String user, BigDecimal amount) {}
