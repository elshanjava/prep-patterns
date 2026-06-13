package creational.factory.good;

// Фабрика: вся логика выбора реализации в одном месте.
// Клиент не знает о CardProcessor, SepaProcessor, CryptoProcessor.
// Добавить Apple Pay: новый ApplePayProcessor + одна строка в switch (OCP).
final class ProcessorFactory {
    static PaymentProcessor create(String method) {
        return switch (method) {
            case "CARD"   -> new CardProcessor();
            case "SEPA"   -> new SepaProcessor();
            case "CRYPTO" -> new CryptoProcessor();
            default       -> throw new IllegalArgumentException("unknown method: " + method);
        };
    }
}
