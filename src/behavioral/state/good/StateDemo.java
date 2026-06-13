package behavioral.state.good;

public class StateDemo {
    public static void main(String[] args) {
        System.out.println("== State [GOOD] ==");

        var payment = new Payment();
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
