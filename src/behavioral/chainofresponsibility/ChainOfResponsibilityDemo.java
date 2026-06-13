package behavioral.chainofresponsibility;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        System.out.println("== Chain of Responsibility ==");

        var pipeline = new FraudPipeline(List.of(
                new LimitCheck(),
                new BlacklistCheck(Set.of("stolen-card-42")),
                new VelocityCheck(Set.of("suspicious-user"))
        ));

        var normal    = new Payment("card-1",         "user-1",          new BigDecimal("100"));
        var overlimit = new Payment("card-2",         "user-2",          new BigDecimal("9999"));
        var blocked   = new Payment("stolen-card-42", "user-3",          new BigDecimal("50"));
        var velocity  = new Payment("card-4",         "suspicious-user", new BigDecimal("10"));

        System.out.println("normal:    " + pipeline.evaluate(normal));    // APPROVE
        System.out.println("overlimit: " + pipeline.evaluate(overlimit)); // DECLINE
        System.out.println("blocked:   " + pipeline.evaluate(blocked));   // DECLINE
        System.out.println("velocity:  " + pipeline.evaluate(velocity));  // REVIEW
    }
}
