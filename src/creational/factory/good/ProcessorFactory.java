package creational.factory.good;

import java.util.Map;
import java.util.function.Supplier;

// Реестр процессоров: добавить GOOGLE_PAY = 1 строка + 1 новый класс.
// Никаких изменений в существующих процессорах, в сервисах, в демо.
final class ProcessorFactory {
    private static final Map<String, Supplier<PaymentProcessor>> REGISTRY = Map.of(
            "CARD",      CardProcessor::new,
            "SEPA",      SepaProcessor::new,
            "CRYPTO",    CryptoProcessor::new,
            "APPLE_PAY", ApplePayProcessor::new
            // "GOOGLE_PAY", GooglePayProcessor::new  ← 1 строка, 1 новый класс
    );

    static PaymentProcessor create(String method) {
        var supplier = REGISTRY.get(method);
        if (supplier == null) throw new IllegalArgumentException("unknown method: " + method);
        return supplier.get();
    }

    static boolean supports(String method) {
        return REGISTRY.containsKey(method);
    }
}
