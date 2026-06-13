package behavioral.interpreter.good;

import behavioral.interpreter.model.Tx;

import java.math.BigDecimal;

public class InterpreterDemo {
    public static void main(String[] args) {
        System.out.println("== Interpreter [GOOD] ==");

        Rule rule = new And(
                new AmountAbove(new BigDecimal("1000")),
                new CountryIs("RU")
        );

        System.out.println("tx1 blocked: " + rule.interpret(new Tx("RU", new BigDecimal("1500")))); // true
        System.out.println("tx2 blocked: " + rule.interpret(new Tx("RU", new BigDecimal("500"))));  // false
        System.out.println("tx3 blocked: " + rule.interpret(new Tx("DE", new BigDecimal("2000")))); // false

        Rule complex = new Or(
                new And(new AmountAbove(new BigDecimal("500")), new CountryIs("DE")),
                new CountryIs("CN")
        );
        System.out.println("tx3 complex: " + complex.interpret(new Tx("DE", new BigDecimal("2000")))); // true
    }
}
