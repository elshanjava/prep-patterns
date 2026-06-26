package coding.behavioral.interpreter;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        // Правило собирается из объектов как дерево:
        // (amount > 1000) AND NOT (country == "DE")
        // "крупный платёж, но НЕ из Германии"
        Expr rule = new And(
                new AmountAbove(new BigDecimal("1000")),
                new Not(new CountryIs("DE"))
        );

        System.out.println("Правило: (amount > 1000) AND NOT (country == DE)");
        System.out.println();

        // Один и тот же объект-правило применяем к разным Tx
        check(rule, new Tx("FR", new BigDecimal("5000")));  // крупный + не DE -> true
        check(rule, new Tx("DE", new BigDecimal("5000")));  // крупный, но DE  -> false
        check(rule, new Tx("FR", new BigDecimal("100")));   // не DE, но мелкий -> false
        check(rule, new Tx("DE", new BigDecimal("100")));   // мелкий и DE     -> false

        // Другое дерево из тех же кирпичиков:
        // (country == DE) OR (amount > 10000)
        System.out.println();
        Expr rule2 = new Or(
                new CountryIs("DE"),
                new AmountAbove(new BigDecimal("10000"))
        );
        System.out.println("Правило2: (country == DE) OR (amount > 10000)");
        System.out.println();
        check(rule2, new Tx("DE", new BigDecimal("50")));    // DE          -> true
        check(rule2, new Tx("FR", new BigDecimal("99999"))); // огромная    -> true
        check(rule2, new Tx("FR", new BigDecimal("50")));    // ни то ни то -> false
    }

    private static void check(Expr rule, Tx tx) {
        System.out.printf("%-30s -> %s%n", tx, rule.evaluate(tx));
    }
}
