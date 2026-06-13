package creational.factory.bad;

// Та же логика — третий раз.
// Каждый раз когда появляется новый метод оплаты, нужно найти ВСЕ три класса.
// Статический анализ не поможет: if/else — не интерфейс, компилятор не следит.
final class BadPaymentValidator {

    void validate(String method, long amountCents) {
        if (amountCents <= 0) throw new IllegalArgumentException("amount must be positive");

        if ("CARD".equals(method)) {
            System.out.println("[card-validate] checking BIN list, expiry window, CVV presence");
            if (amountCents > 500_000_00L)
                throw new IllegalArgumentException("[card] exceeds single-txn limit 500 000 EUR");

        } else if ("SEPA".equals(method)) {
            System.out.println("[sepa-validate] IBAN checksum, BIC reachability, mandate status");
            if (amountCents > 100_000_00L)
                throw new IllegalArgumentException("[sepa] exceeds SEPA CT limit 100 000 EUR");

        } else if ("CRYPTO".equals(method)) {
            System.out.println("[crypto-validate] address format, dust threshold, AML screening");
            if (amountCents < 1_000L)
                throw new IllegalArgumentException("[crypto] below dust threshold");

        } else {
            throw new IllegalArgumentException("unknown method: " + method);
        }
        // APPLE_PAY: забыли добавить → принимаем платёж без валидации суммы и лимитов
    }
}
