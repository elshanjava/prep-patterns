package behavioral.chainofresponsibility.bad;

import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.Set;

public class BadDemo {
    public static void main(String[] args) {
        System.out.println("== Chain of Responsibility [BAD] ==");
        var service = new FraudService(Set.of("stolen-card-42"), Set.of("suspicious-user"));
        System.out.println(service.check(new Payment("card-1",         "user-1",          new BigDecimal("100"))));
        System.out.println(service.check(new Payment("card-2",         "user-2",          new BigDecimal("9999"))));
        System.out.println(service.check(new Payment("stolen-card-42", "user-3",          new BigDecimal("50"))));
        System.out.println(service.check(new Payment("card-4",         "suspicious-user", new BigDecimal("10"))));
    }
}
