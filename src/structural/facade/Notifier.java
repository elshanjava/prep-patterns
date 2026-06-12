package structural.facade;

// Подсистема №5: уведомления клиенту.
final class Notifier {
    void send(String customer, Authorization auth) {
        System.out.println("  [notify] " + customer + " <- " + auth.id());
    }
}
