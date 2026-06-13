package behavioral.mediator.model;

public final class NotificationService {
    public void send(Order o) { System.out.println("notifier: sent for " + o.id()); }
}
