package coding.behavioral.visitor;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        // дерево: (2 + 3) * 4
        //       Mul
        //      /   \
        //    Add    Number(4)
        //   /   \
        // Num(2) Num(3)
        OperationNode tree = new MulNode(
                new AddNode(
                        new NumberNode(new BigDecimal("2")),
                        new NumberNode(new BigDecimal("3"))
                ),
                new NumberNode(new BigDecimal("4"))
        );

        // одна структура, две операции — узлы не знают ни про Calculate, ни про Print
        String infix = tree.accept(new PrintVisitor());
        BigDecimal result = tree.accept(new CalculateVisitor());

        System.out.println("выражение: " + infix);
        System.out.println("результат: " + result);

        // ещё одно: 10 * (5 + 1)
        System.out.println();
        OperationNode tree2 = new MulNode(
                new NumberNode(new BigDecimal("10")),
                new AddNode(
                        new NumberNode(new BigDecimal("5")),
                        new NumberNode(new BigDecimal("1"))
                )
        );
        System.out.println("выражение: " + tree2.accept(new PrintVisitor()));
        System.out.println("результат: " + tree2.accept(new CalculateVisitor()));
    }
}
