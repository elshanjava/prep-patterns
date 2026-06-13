package structural.bridge.good;

// Добавить 4-й канал: 1 новый класс — 0 изменений в Notification-классах.
final class WhatsAppChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[whatsapp → " + recipient + "] " + message);
    }
}
