package behavioral.chainofresponsibility.good;

import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        System.out.println("== Chain of Responsibility [GOOD] ==");

        var pipeline = new FraudPipeline(List.of(
                new LimitCheck(),
                new BlacklistCheck(Set.of("stolen-card-42")),
                new VelocityCheck(Set.of("suspicious-user"))
        ));

        System.out.println(pipeline.evaluate(new Payment("card-1",         "user-1",          new BigDecimal("100"))));
        System.out.println(pipeline.evaluate(new Payment("card-2",         "user-2",          new BigDecimal("9999"))));
        System.out.println(pipeline.evaluate(new Payment("stolen-card-42", "user-3",          new BigDecimal("50"))));
        System.out.println(pipeline.evaluate(new Payment("card-4",         "suspicious-user", new BigDecimal("10"))));
    }
}
