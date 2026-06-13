package structural.bridge.good;

final class SmsChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[sms → " + recipient + "] " + message);
    }
}
