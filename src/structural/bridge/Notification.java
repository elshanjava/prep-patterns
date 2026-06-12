package structural.bridge;

abstract class Notification {
    protected final MessageChannel channel;       // МОСТ: ссылка на реализацию
    protected Notification(MessageChannel channel) { this.channel = channel; }
    abstract void notify(String to, String text);
}
 
