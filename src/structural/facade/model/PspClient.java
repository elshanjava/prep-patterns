package structural.facade.model;

import java.math.BigDecimal;

public final class PspClient {
    public Authorization authorize(Order order, BigDecimal rate) {
        System.out.println("  [psp] authorize " + order.amountCents() + " @ rate " + rate);
        return new Authorization("auth_" + order.customer(), order.amountCents());
    }
}
