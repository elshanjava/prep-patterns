package structural.facade;

import java.math.BigDecimal;

// Подсистема №3: клиент платёжного провайдера.
final class PspClient {
    Authorization authorize(Order order, BigDecimal rate) {
        System.out.println("  [psp] authorize " + order.amountCents() + " @ rate " + rate);
        return new Authorization("auth_" + order.customer(), order.amountCents());
    }
}
