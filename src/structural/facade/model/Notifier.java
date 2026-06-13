package structural.facade.model;

public final class Notifier {
    public void send(String customer, Authorization auth) {
        System.out.println("  [notify] payment  " + auth.id() + " → " + customer);
    }

    public void sendRefund(String customer, Authorization auth) {
        System.out.println("  [notify] refund   " + auth.id() + " → " + customer);
    }
}
