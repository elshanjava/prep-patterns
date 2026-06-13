package structural.bridge.good;

// Implementor: транспортный уровень — КАК доставить сообщение.
// Развивается независимо от логики уведомлений.
interface MessageChannel {
    void send(String recipient, String message);
}
