package behavioral.strategy.bad;

import behavioral.strategy.model.Payment;

import java.math.BigDecimal;

// Тот же switch что в BadFeeCalculator — скопипащен в другой класс.
// Добавить APPLE_PAY → правь BadFeeCalculator И BadReceiptGenerator.
// Пропустить одно место → квитанция выдаётся без правильной подписи.
final class BadReceiptGenerator {
    String generate(Payment p) {
        return switch (p.method()) {
            case CARD   -> "Card receipt: charged " + p.amount() + " (Visa/MC network)";
            case SEPA   -> "SEPA receipt: bank transfer " + p.amount() + " (T+1 settlement)";
            case CRYPTO -> "Crypto receipt: blockchain tx " + p.amount() + " (3 confirmations)";
            // APPLE_PAY здесь ещё нет — хотя в fee-калькуляторе уже добавили
        };
    }
}
