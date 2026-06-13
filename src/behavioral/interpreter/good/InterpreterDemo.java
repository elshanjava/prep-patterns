package behavioral.interpreter.good;

import behavioral.interpreter.model.Tx;

import java.math.BigDecimal;
import java.util.List;

public class InterpreterDemo {
    public static void main(String[] args) {
        System.out.println("== Interpreter [GOOD] ==");

        // --- 1. Атомарные правила — тестируются изолированно ---
        // В bad/ нельзя протестировать отдельный предикат без парсера строки.
        System.out.println("-- 1. Атомарные правила изолированно --");
        Rule amountAbove1000 = new AmountAbove(new BigDecimal("1000"));
        Rule amountAbove500  = new AmountAbove(new BigDecimal("500"));
        Rule amountAbove100  = new AmountAbove(new BigDecimal("100"));
        Rule countryRu       = new CountryIs("RU");
        Rule countryDe       = new CountryIs("DE");
        Rule countryCn       = new CountryIs("CN");

        System.out.println("amountAbove1000 on 1500: " + amountAbove1000.interpret(new Tx("RU", new BigDecimal("1500")))); // true
        System.out.println("countryRu on DE:         " + countryRu.interpret(new Tx("DE", new BigDecimal("100"))));         // false

        // --- 2. AND-правило ---
        System.out.println();
        System.out.println("-- 2. AND-правило: amount>1000 AND country==RU --");
        Rule andRule = new And(amountAbove1000, countryRu);
        System.out.println("tx RU/1500: " + andRule.interpret(new Tx("RU", new BigDecimal("1500")))); // true
        System.out.println("tx RU/500:  " + andRule.interpret(new Tx("RU", new BigDecimal("500"))));  // false
        System.out.println("tx DE/2000: " + andRule.interpret(new Tx("DE", new BigDecimal("2000")))); // false

        // --- 3. OR-правило (в bad/ молча возвращал неверный результат) ---
        System.out.println();
        System.out.println("-- 3. OR-правило: country==RU OR country==CN --");
        Rule orRule = new Or(countryRu, countryCn);
        System.out.println("tx RU/100 (true):  " + orRule.interpret(new Tx("RU", new BigDecimal("100"))));  // true
        System.out.println("tx CN/100 (true):  " + orRule.interpret(new Tx("CN", new BigDecimal("100"))));  // true
        System.out.println("tx DE/100 (false): " + orRule.interpret(new Tx("DE", new BigDecimal("100")))); // false

        // --- 4. Сложное вложенное правило (в bad/ невозможно выразить строкой) ---
        // (amount>500 AND country==DE) OR (country==CN AND NOT amount>100)
        // Читается как: высокая сумма в DE  ИЛИ  малая сумма из CN (подозрительно)
        System.out.println();
        System.out.println("-- 4. Вложенное: (amount>500 AND country==DE) OR (country==CN AND NOT amount>100) --");
        Rule complexRule = new Or(
                new And(amountAbove500, countryDe),
                new And(countryCn, new Not(amountAbove100))
        );
        System.out.println("tx DE/600 (true):  " + complexRule.interpret(new Tx("DE", new BigDecimal("600"))));  // DE + >500 → true
        System.out.println("tx DE/400 (false): " + complexRule.interpret(new Tx("DE", new BigDecimal("400")))); // DE + <=500 → false
        System.out.println("tx CN/50  (true):  " + complexRule.interpret(new Tx("CN", new BigDecimal("50"))));   // CN + <=100 → true
        System.out.println("tx CN/200 (false): " + complexRule.interpret(new Tx("CN", new BigDecimal("200")))); // CN + >100 → false
        System.out.println("tx US/999 (false): " + complexRule.interpret(new Tx("US", new BigDecimal("999")))); // ни DE ни CN → false

        // --- 5. Три разных правила из одних атомов ---
        System.out.println();
        System.out.println("-- 5. Три правила из одних атомов (переиспользование) --");
        Rule highValueRu       = new And(amountAbove1000, countryRu);
        Rule anyBlockedCountry = new Or(countryRu, new Or(countryCn, new CountryIs("IR")));
        Rule lowRiskDe         = new And(countryDe, new Not(amountAbove1000));

        var testTx = new Tx("RU", new BigDecimal("1500"));
        System.out.println("highValueRu  on RU/1500: " + highValueRu.interpret(testTx));       // true
        System.out.println("anyBlocked   on RU/1500: " + anyBlockedCountry.interpret(testTx)); // true
        System.out.println("lowRiskDe    on RU/1500: " + lowRiskDe.interpret(testTx));         // false

        // --- 6. Батч: правило компилируется 1 раз, применяется N раз ---
        // В bad/ строка разбиралась заново для каждой транзакции (дублирование).
        System.out.println();
        System.out.println("-- 6. Батч: andRule применяется N раз без повторного парсинга --");
        List<Tx> batch = List.of(
                new Tx("RU", new BigDecimal("1500")),
                new Tx("DE", new BigDecimal("2000")),
                new Tx("RU", new BigDecimal("500")),
                new Tx("CN", new BigDecimal("300"))
        );
        batch.forEach(tx -> System.out.printf("  %s/%s → %s%n",
                tx.country(), tx.amount().toPlainString(), andRule.interpret(tx)));

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - OR работает корректно: new Or(countryRu, countryCn)");
        System.out.println("  - вложенные скобки любой глубины: те же атомарные объекты");
        System.out.println("  - атомы тестируются изолированно: new AmountAbove(1000).interpret(tx)");
        System.out.println("  - правило компилируется 1 раз, применяется N раз без повторного разбора");
        System.out.println("  - добавить оператор XOR = 1 файл XorRule.java implements Rule");
    }
}
