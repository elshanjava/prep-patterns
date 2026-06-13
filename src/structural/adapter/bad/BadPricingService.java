package structural.adapter.bad;

import structural.adapter.model.LegacyFxSdk;

import java.math.BigDecimal;

// Клиент напрямую работает с кривым API легаси-SDK:
// — знает, что центы нужно передавать как double
// — знает, что коды валют — строки, а не Currency
// — вручную переводит основные единицы ↔ центы в каждом методе
// — если SDK изменит API, правь КАЖДЫЙ такой вызов по всему коду
final class BadPricingService {
    private final LegacyFxSdk sdk = new LegacyFxSdk();

    BigDecimal quote(BigDecimal eurAmount, String targetCode) {
        // клиент вынужден знать внутренности SDK: double, string-коды, ручная конвертация
        double cents = eurAmount.multiply(BigDecimal.valueOf(100)).doubleValue();
        BigDecimal resultCents = sdk.convertMoney("EUR", targetCode, cents);
        return resultCents.movePointLeft(2);  // обратная конвертация — дублируется везде
    }

    BigDecimal fee(BigDecimal usdAmount) {
        // и здесь та же самая ручная конвертация — скопипащена
        double cents = usdAmount.multiply(BigDecimal.valueOf(100)).doubleValue();
        BigDecimal converted = sdk.convertMoney("USD", "EUR", cents);
        return converted.movePointLeft(2).multiply(new BigDecimal("0.029"));
        // sdk поменяет double на long? — правим здесь, в quote() и в 10 других местах
    }
}
