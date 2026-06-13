package creational.factory.bad;

// Та же логика выбора по методу оплаты — скопипащена второй раз.
// Добавить APPLE_PAY → правь BadPaymentProcessor И BadFeeCalculator И BadPaymentValidator.
// Пропустить хотя бы один — баг в проде.
final class BadFeeCalculator {
    long calculate(String method, long amountCents) {
        if ("CARD".equals(method)) {
            // interchange 1.5% + scheme 0.5% + markup 0.8%
            return Math.round(amountCents * 0.0280);

        } else if ("SEPA".equals(method)) {
            // фиксированная + переменная, минимум 30 центов
            return Math.max(30L, Math.round(amountCents * 0.003));

        } else if ("CRYPTO".equals(method)) {
            // network fee + custody spread
            return Math.round(amountCents * 0.010) + 500;

        } else {
            throw new IllegalArgumentException("unknown method: " + method);
        }
        // APPLE_PAY здесь нет — хотя BadPaymentProcessor его уже поддерживает.
        // Принимаем платёж, но комиссию рассчитываем неверно (или падаем с exception).
    }
}
