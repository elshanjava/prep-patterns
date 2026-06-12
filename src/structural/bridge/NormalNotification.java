package structural.bridge;

final class NormalNotification extends Notification {
    NormalNotification(MessageChannel channel) { super(channel); }
    void notify(String to, String text) { channel.send(to, text); }
}
// new UrgentNotification(new SmsChannel()).notify(...);  -> любая комбинация M+N классов
