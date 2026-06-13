package behavioral.state.bad;

public class BadStateDemo {
    public static void main(String[] args) {
        System.out.println("== State [BAD] ==");

        var payment = new BadPayment();
        System.out.println("initial:       " + payment.status());
        payment.capture();
        System.out.println("after capture: " + payment.status());
        payment.refund();
        System.out.println("after refund:  " + payment.status());

        try {
            payment.refund();
        } catch (IllegalStateException e) {
            System.out.println("blocked: " + e.getMessage());
        }
    }
}
