package behavioral.chainofresponsibility.bad;

import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class BadDemo {
    public static void main(String[] args) {
        System.out.println("== Chain of Responsibility [BAD] ==");

        var service = new FraudService(
                Set.of("stolen-card-42"),
                Set.of("suspicious-user"),
                Set.of("IR", "KP", "CU")   // GEO-список заблокированных стран
        );

        System.out.println("-- одиночные проверки (5 сценариев) --");
        // 1. Обычный платёж — должен пройти
        System.out.println(service.check(new Payment("card-1",         "user-1",          new BigDecimal("100"),   "DE")));
        // 2. Превышение лимита
        System.out.println(service.check(new Payment("card-2",         "user-2",          new BigDecimal("9999"),  "US")));
        // 3. Украденная карта
        System.out.println(service.check(new Payment("stolen-card-42", "user-3",          new BigDecimal("50"),    "GB")));
        // 4. Подозрительный пользователь
        System.out.println(service.check(new Payment("card-4",         "suspicious-user", new BigDecimal("10"),    "FR")));
        // 5. GEO-блокировка: страна в стоп-листе — добавление этого правила потребовало
        //    редактировать FraudService.check() И FraudService.checkBatch() — два места
        System.out.println(service.check(new Payment("card-5",         "user-5",          new BigDecimal("200"),   "IR")));

        System.out.println();
        System.out.println("-- батч-обработка (тот же if-каскад скопирован в checkBatch) --");
        var batch = List.of(
                new Payment("card-ok",         "user-ok",         new BigDecimal("50"),   "US"),
                new Payment("stolen-card-42",  "user-ok",         new BigDecimal("50"),   "US"),
                new Payment("card-geo",        "user-ok",         new BigDecimal("200"),  "KP")
        );
        List<behavioral.chainofresponsibility.model.Decision> results = service.checkBatch(batch);
        for (int i = 0; i < batch.size(); i++) {
            System.out.println(batch.get(i).card() + " → " + results.get(i));
        }

        System.out.println();
        // Проблемы:
        // 1. Добавить GEO-правило = отредактировать check() И checkBatch() — оба метода растут.
        // 2. Переставить порядок «лимит перед GEO» vs «GEO перед лимитом» = хирургическая правка метода.
        // 3. Протестировать только GEO-правило изолированно невозможно — оно зашито внутри.
        // 4. В checkBatch нет вызова check() — логика продублирована; рассинхронизация гарантирована.
        System.out.println("Проблемы:");
        System.out.println("  - добавить 5-е правило = правка check() + checkBatch()");
        System.out.println("  - переставить порядок  = ручная хирургия в if-каскаде");
        System.out.println("  - тест GEO изолированно = невозможно без создания FraudService целиком");
    }
}
