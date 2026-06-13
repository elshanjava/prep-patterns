package structural.bridge.good;

final class PushChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[push → " + recipient + "] " + message);
    }
}
