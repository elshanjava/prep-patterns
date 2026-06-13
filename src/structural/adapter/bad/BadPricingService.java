package structural.adapter.bad;

import structural.adapter.model.LegacyFxSdk;

import java.math.BigDecimal;

// Каждый метод вручную переводит основные единицы ↔ центы через double.
// 5 методов = 5 одинаковых блоков boilerplate.
// SDK изменит тип с double на long? → правь все 5 методов.
final class BadPricingService {
    private final LegacyFxSdk sdk = new LegacyFxSdk();

    BigDecimal quote(BigDecimal eurAmount, String targetCode) {
        double cents = eurAmount.multiply(BigDecimal.valueOf(100)).doubleValue(); // boilerplate #1
        return sdk.convertMoney("EUR", targetCode, cents).movePointLeft(2);
    }

    BigDecimal fee(BigDecimal usdAmount) {
        double cents = usdAmount.multiply(BigDecimal.valueOf(100)).doubleValue(); // boilerplate #2
        return sdk.convertMoney("USD", "EUR", cents).movePointLeft(2)
                  .multiply(new BigDecimal("0.029"));
    }

    BigDecimal hedgeExposure(BigDecimal gbpExposure) {
        double cents = gbpExposure.multiply(BigDecimal.valueOf(100)).doubleValue(); // boilerplate #3
        return sdk.convertMoney("GBP", "USD", cents).movePointLeft(2);
    }

    BigDecimal convertForPayout(BigDecimal amount, String fromCcy) {
        double cents = amount.multiply(BigDecimal.valueOf(100)).doubleValue(); // boilerplate #4
        return sdk.convertMoney(fromCcy, "EUR", cents).movePointLeft(2);
    }

    BigDecimal markToMarket(BigDecimal usdPosition) {
        double cents = usdPosition.multiply(BigDecimal.valueOf(100)).doubleValue(); // boilerplate #5
        return sdk.convertMoney("USD", "EUR", cents).movePointLeft(2);
        // SDK сменит double → long: правь здесь + в quote() + fee() + hedgeExposure() + convertForPayout()
    }
}
