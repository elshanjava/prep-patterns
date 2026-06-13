package behavioral.strategy.bad;

import behavioral.strategy.model.Payment;
import behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;

public class BadStrategyDemo {
    public static void main(String[] args) {
        System.out.println("== Strategy [BAD] ==");
        var calc = new BadFeeCalculator();
        System.out.printf("CARD fee: %.4f%n", calc.fee(new Payment(PaymentMethod.CARD, new BigDecimal("100"))));
        System.out.printf("SEPA fee: %.4f%n", calc.fee(new Payment(PaymentMethod.SEPA, new BigDecimal("100"))));
    }
}
