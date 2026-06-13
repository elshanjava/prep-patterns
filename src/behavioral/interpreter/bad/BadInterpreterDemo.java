package behavioral.interpreter.bad;

import behavioral.interpreter.model.Tx;

import java.math.BigDecimal;

public class BadInterpreterDemo {
    public static void main(String[] args) {
        System.out.println("== Interpreter [BAD] ==");
        var checker = new BadRuleChecker();

        // правила как строки — хрупко, не масштабируется
        var rule = "amount>1000 AND country==RU";
        System.out.println("tx1 blocked: " + checker.check(rule, new Tx("RU", new BigDecimal("1500")))); // true
        System.out.println("tx2 blocked: " + checker.check(rule, new Tx("RU", new BigDecimal("500"))));  // false
        System.out.println("tx3 blocked: " + checker.check(rule, new Tx("DE", new BigDecimal("2000")))); // false
    }
}
