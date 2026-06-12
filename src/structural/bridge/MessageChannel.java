package structural.bridge;

interface MessageChannel {                       // implementor
    void send(String to, String body);
}
