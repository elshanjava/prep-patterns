package behavioral.strategy;

import java.math.BigDecimal;
import java.util.List;

public class StrategyDemo {
    public static void main(String[] args) {
        System.out.println("== Strategy ==");

        var calculator = new FeeCalculator(List.of(new CardFee(), new SepaFee()));

        var card = new Payment(PaymentMethod.CARD, new BigDecimal("100.00"));
        var sepa = new Payment(PaymentMethod.SEPA, new BigDecimal("100.00"));

        System.out.printf("CARD fee: %.4f%n", calculator.fee(card));
        System.out.printf("SEPA fee: %.4f%n", calculator.fee(sepa));

        try {
            calculator.fee(new Payment(PaymentMethod.CRYPTO, BigDecimal.TEN));
        } catch (UnsupportedOperationException e) {
            System.out.println("no strategy for: " + e.getMessage());
        }
    }
}
