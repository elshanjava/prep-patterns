package structural.bridge;

final class UrgentNotification extends Notification {
    UrgentNotification(MessageChannel channel) { super(channel); }
    void notify(String to, String text) {
        channel.send(to, "[СРОЧНО] " + text);     // делегирует каналу, не зная какому
    }
}
