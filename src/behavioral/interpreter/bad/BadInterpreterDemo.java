package behavioral.interpreter.bad;

import behavioral.interpreter.model.Tx;

import java.math.BigDecimal;
import java.util.List;

public class BadInterpreterDemo {
    public static void main(String[] args) {
        System.out.println("== Interpreter [BAD] ==");

        var checker = new BadRuleChecker();

        // --- AND-правила работают ---
        System.out.println("-- AND-правила (работают) --");
        var andRule = "amount>1000 AND country==RU";
        System.out.println("tx RU/1500: " + checker.check(andRule, new Tx("RU", new BigDecimal("1500")))); // true
        System.out.println("tx RU/500:  " + checker.check(andRule, new Tx("RU", new BigDecimal("500"))));  // false
        System.out.println("tx DE/2000: " + checker.check(andRule, new Tx("DE", new BigDecimal("2000")))); // false

        // --- OR молча возвращает неверный результат ---
        System.out.println();
        System.out.println("-- OR-правило (МОЛЧА ИГНОРИРУЕТСЯ — баг!) --");
        // Ожидание: заблокировать транзакции из RU ИЛИ из CN
        // Реальность: "OR" не распознан → split(" AND ") не делит строку → вся строка = 1 часть
        // Первая часть — "country==RU OR country==CN" — не совпадает ни с одним if → пропуск → always true!
        var orRule = "country==RU OR country==CN";
        System.out.println("tx CN/100 (ожидаем true, заблокировано): "
                + checker.check(orRule, new Tx("CN", new BigDecimal("100"))));
        // Выведет: true — но не потому что OR сработал, а потому что парсер ничего не нашёл!
        System.out.println("tx DE/100 (ожидаем false, не заблокировано): "
                + checker.check(orRule, new Tx("DE", new BigDecimal("100"))));
        // Выведет: true — НЕВЕРНО! Парсер проигнорировал OR, вернул true для всех

        // --- Вложенные скобки не поддерживаются вообще ---
        System.out.println();
        System.out.println("-- вложенные скобки (не поддерживаются) --");
        // Это правило нельзя выразить строкой: "(amount>500 AND country==DE) OR country==CN"
        // В good/ — тривиально: new Or(new And(amountAbove500, countryDe), countryCn)
        System.out.println("сложное правило '(amount>500 AND country==DE) OR country==CN' — невыразимо строкой");

        // --- Батч с дублированием парсинга ---
        System.out.println();
        System.out.println("-- checkBatch: строка разбирается для каждой транзакции заново --");
        var batch = List.of(
                new Tx("RU", new BigDecimal("1500")),
                new Tx("DE", new BigDecimal("2000")),
                new Tx("RU", new BigDecimal("500"))
        );
        var batchResults = checker.checkBatch("amount>1000 AND country==RU", batch);
        System.out.println("batch results: " + batchResults);

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - OR не работает: молча возвращает true вместо IllegalArgumentException");
        System.out.println("  - вложенные скобки невозможны: нет AST, только линейный разбор строки");
        System.out.println("  - checkBatch разбирает строку N раз: нет компиляции правила");
        System.out.println("  - добавить NOT = переписать парсер, рискуя сломать AND");
    }
}
