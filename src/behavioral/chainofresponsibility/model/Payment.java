package behavioral.chainofresponsibility.model;

import java.math.BigDecimal;

public record Payment(String card, String user, BigDecimal amount) {}
