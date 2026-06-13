package structural.facade.model;

public final class Notifier {
    public void send(String customer, Authorization auth) {
        System.out.println("  [notify] " + customer + " <- " + auth.id());
    }
}
