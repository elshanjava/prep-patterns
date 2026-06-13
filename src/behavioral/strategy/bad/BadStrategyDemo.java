package behavioral.strategy.bad;

import behavioral.strategy.model.Payment;
import behavioral.strategy.model.PaymentMethod;

import java.math.BigDecimal;

public class BadStrategyDemo {
    public static void main(String[] args) {
        System.out.println("== Strategy [BAD] ==");

        var calc      = new BadFeeCalculator();
        var receipts  = new BadReceiptGenerator();

        // Три метода оплаты — одинаковые switch в обоих классах
        for (PaymentMethod method : PaymentMethod.values()) {
            var payment = new Payment(method, new BigDecimal("100.00"));
            System.out.printf("%-6s fee: %s%n", method, calc.fee(payment).toPlainString());
            System.out.printf("%-6s rcv: %s%n", method, receipts.generate(payment));
            System.out.println();
        }

        // Добавить APPLE_PAY:
        //   1. Добавить в PaymentMethod enum
        //   2. Добавить case в BadFeeCalculator.fee()
        //   3. Добавить case в BadReceiptGenerator.generate()
        //   Забыть шаг 3 → квитанция бросит MatchException в рантайме.
        System.out.println("Проблемы:");
        System.out.println("  - добавить APPLE_PAY = правь 2 switch'а в 2 разных классах");
        System.out.println("  - алгоритм расчёта комиссии продублирован в BadReceiptGenerator");
        System.out.println("  - тест отдельного алгоритма невозможен без создания BadFeeCalculator целиком");
        System.out.println("  - рассинхронизация fee и receipt-логики = баг без ошибки компиляции");
    }
}
