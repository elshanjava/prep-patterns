package structural.bridge.good;

// Abstraction: бизнес-уровень — ЧТО и КОМУ сообщить.
// Канал передаётся снаружи — Notification и MessageChannel развиваются независимо.
abstract class Notification {
    protected final MessageChannel channel;

    Notification(MessageChannel channel) { this.channel = channel; }

    abstract void send(String recipient, String event);
}
