package structural.bridge.good;

final class UrgentNotification extends Notification {
    UrgentNotification(MessageChannel channel) { super(channel); }

    @Override
    public void send(String recipient, String event) {
        channel.send(recipient, "[СРОЧНО] " + event + " — требуется немедленное действие!");
    }
}
