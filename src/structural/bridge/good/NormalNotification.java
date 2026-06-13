package structural.bridge.good;

final class NormalNotification extends Notification {
    NormalNotification(MessageChannel channel) { super(channel); }

    @Override
    public void send(String recipient, String event) {
        channel.send(recipient, "Уведомление: " + event);
    }
}
