package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        System.out.println("== Chain of Responsibility [GOOD] ==");

        // --- 1. Стандартный пайплайн (3 проверки) ---
        System.out.println("-- 1. Стандартный пайплайн --");
        var standardPipeline = new FraudPipeline(List.of(
                new LimitCheck(),
                new BlacklistCheck(Set.of("stolen-card-42")),
                new VelocityCheck(Set.of("suspicious-user"))
        ));

        System.out.println(standardPipeline.evaluate(new Payment("card-1",         "user-1",          new BigDecimal("100"),   "DE")));
        System.out.println(standardPipeline.evaluate(new Payment("card-2",         "user-2",          new BigDecimal("9999"),  "US")));
        System.out.println(standardPipeline.evaluate(new Payment("stolen-card-42", "user-3",          new BigDecimal("50"),    "GB")));
        System.out.println(standardPipeline.evaluate(new Payment("card-4",         "suspicious-user", new BigDecimal("10"),    "FR")));

        // --- 2. Строгий пайплайн: GeoCheck добавлен без правки существующего кода ---
        // Добавление GEO = 1 новый файл GeoCheck.java + 1 строка в списке ниже.
        // FraudPipeline, LimitCheck, BlacklistCheck, VelocityCheck — не трогались.
        System.out.println();
        System.out.println("-- 2. Строгий пайплайн с GeoCheck (добавление = 1 файл + 1 строка) --");
        var strictPipeline = new FraudPipeline(List.of(
                new GeoCheck(Set.of("IR", "KP", "CU")),  // GEO стоит первым — без правки остальных
                new LimitCheck(),
                new BlacklistCheck(Set.of("stolen-card-42")),
                new VelocityCheck(Set.of("suspicious-user"))
        ));

        System.out.println(strictPipeline.evaluate(new Payment("card-5", "user-5", new BigDecimal("200"), "IR")));   // DECLINE (GEO)
        System.out.println(strictPipeline.evaluate(new Payment("card-6", "user-6", new BigDecimal("50"),  "KP")));   // DECLINE (GEO)
        System.out.println(strictPipeline.evaluate(new Payment("card-7", "user-7", new BigDecimal("50"),  "DE")));   // APPROVE

        // --- 3. Изолированное тестирование одного звена ---
        // В bad/ невозможно протестировать одно правило без FraudService целиком.
        // Здесь: LimitCheck тестируется сам по себе, без пайплайна.
        System.out.println();
        System.out.println("-- 3. Изолированный тест LimitCheck (без пайплайна) --");
        var limitCheck = new LimitCheck();
        System.out.println("10000 → " + limitCheck.check(new Payment("card-x", "user-x", new BigDecimal("10000"), "DE")));  // DECLINE
        System.out.println("100   → " + limitCheck.check(new Payment("card-x", "user-x", new BigDecimal("100"),   "DE")));  // empty (продолжить)

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - добавить GEO = 1 файл GeoCheck.java, остальные классы не трогать");
        System.out.println("  - переставить порядок = поменять строку в списке List.of(...)");
        System.out.println("  - тест изолированно  = new LimitCheck().check(payment) без зависимостей");
        System.out.println("  - в Spring: @Component + @Order — порядок из конфига, не из кода");
    }
}
