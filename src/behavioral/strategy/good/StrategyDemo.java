package behavioral.strategy.good;

import behavioral.strategy.model.Payment;
import behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public class StrategyDemo {
    public static void main(String[] args) {
        System.out.println("== Strategy [GOOD] ==");

        // --- 1. Все 3 стратегии ---
        System.out.println("-- 1. Три стратегии --");
        var calculator = new FeeCalculator(List.of(new CardFee(), new SepaFee(), new CryptoFee()));

        System.out.printf("CARD   fee: %.4f%n", calculator.fee(new Payment(PaymentMethod.CARD,   new BigDecimal("100.00"))));
        System.out.printf("SEPA   fee: %.4f%n", calculator.fee(new Payment(PaymentMethod.SEPA,   new BigDecimal("100.00"))));
        System.out.printf("CRYPTO fee: %.4f%n", calculator.fee(new Payment(PaymentMethod.CRYPTO, new BigDecimal("100.00"))));

        // --- 2. Динамический выбор стратегии ---
        System.out.println();
        System.out.println("-- 2. Динамический выбор по методу оплаты --");
        List<Payment> payments = List.of(
                new Payment(PaymentMethod.CARD,   new BigDecimal("250.00")),
                new Payment(PaymentMethod.SEPA,   new BigDecimal("1200.00")),
                new Payment(PaymentMethod.CRYPTO, new BigDecimal("500.00"))
        );
        payments.forEach(p ->
                System.out.printf("%-6s amount=%s fee=%s%n",
                        p.method(), p.amount().toPlainString(),
                        calculator.fee(p).toPlainString()));

        // --- 3. Добавление CryptoFee: только 1 файл, без правки существующих ---
        // CryptoFee.java уже включён в List.of выше.
        // В Spring: @Component на CryptoFee + @Autowired List<FeeStrategy> в FeeCalculator —
        // CryptoFee подхватывается автоматически без правки FeeCalculator.
        System.out.println();
        System.out.println("-- 3. Неизвестный метод: UnsupportedOperationException --");
        // Симуляция: FeeCalculator без CryptoFee не знает о CRYPTO
        var limitedCalc = new FeeCalculator(List.of(new CardFee(), new SepaFee()));
        try {
            limitedCalc.fee(new Payment(PaymentMethod.CRYPTO, BigDecimal.TEN));
        } catch (UnsupportedOperationException e) {
            System.out.println("no strategy for: " + e.getMessage());
        }

        // --- 4. Тест стратегии изолированно ---
        System.out.println();
        System.out.println("-- 4. Изолированный тест CardFee (без FeeCalculator) --");
        var cardFee = new CardFee();
        System.out.printf("CardFee на 200: %.4f%n",
                cardFee.fee(new Payment(PaymentMethod.CARD, new BigDecimal("200.00"))));

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - добавить APPLE_PAY = 1 файл ApplePayFee.java + @Component в Spring");
        System.out.println("  - FeeCalculator, CardFee, SepaFee, CryptoFee — не трогаются");
        System.out.println("  - нет дублирования: receipt-логика тоже будет отдельной стратегией");
        System.out.println("  - тест CardFee: new CardFee().fee(payment) — без зависимостей");
        System.out.println("  - Spring DI: @Autowired List<FeeStrategy> — автосборка без правки кода");
    }
}
