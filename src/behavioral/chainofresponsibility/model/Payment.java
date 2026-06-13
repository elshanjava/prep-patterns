package behavioral.chainofresponsibility.model;

import java.math.BigDecimal;

// Добавили country для GEO-проверки
public record Payment(String card, String user, BigDecimal amount, String country) {
    // Удобный конструктор без country для обратной совместимости со старыми тестами
    public Payment(String card, String user, BigDecimal amount) {
        this(card, user, amount, "");
    }
}
