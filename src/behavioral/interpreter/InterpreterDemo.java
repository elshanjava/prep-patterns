package behavioral.interpreter;

import java.math.BigDecimal;

public class InterpreterDemo {
    public static void main(String[] args) {
        System.out.println("== Interpreter ==");

        // "amount > 1000 AND country == RU" как дерево объектов
        Rule rule = new And(
                new AmountAbove(new BigDecimal("1000")),
                new CountryIs("RU")
        );

        var tx1 = new Tx("RU", new BigDecimal("1500"));  // blocked
        var tx2 = new Tx("RU", new BigDecimal("500"));   // amount too low
        var tx3 = new Tx("DE", new BigDecimal("2000"));  // wrong country

        System.out.println("tx1 blocked: " + rule.interpret(tx1));   // true
        System.out.println("tx2 blocked: " + rule.interpret(tx2));   // false
        System.out.println("tx3 blocked: " + rule.interpret(tx3));   // false

        // "(amount > 500 AND country == DE) OR country == CN"
        Rule complex = new Or(
                new And(new AmountAbove(new BigDecimal("500")), new CountryIs("DE")),
                new CountryIs("CN")
        );
        System.out.println("tx3 complex: " + complex.interpret(tx3)); // true
    }
}
