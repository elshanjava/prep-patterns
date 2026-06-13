package structural.bridge.good;

// Добавить 3-й уровень: 1 новый класс — 0 изменений в каналах.
final class CriticalNotification extends Notification {
    CriticalNotification(MessageChannel channel) { super(channel); }

    @Override
    public void send(String recipient, String event) {
        channel.send(recipient, "🚨🚨 CRITICAL: " + event + " — IMMEDIATE ACTION REQUIRED");
    }
}
