package structural.bridge.good;

final class EmailChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[email → " + recipient + "] " + message);
    }
}
