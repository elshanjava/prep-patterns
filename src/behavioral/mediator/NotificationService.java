package behavioral.mediator;

final class NotificationService {
    void send(Order o) { System.out.println("notifier: sent for " + o.id()); }
}
