package behavioral.mediator.model;

public final class AuditService {
    public void log(String event, Order o) {
        System.out.println("audit: event=" + event + " order=" + o.id());
    }
}
