package coding.structural.bridge;

public abstract class NotificationType {
  final Channel channel;

  NotificationType(Channel channel) {
    this.channel = channel;
  }

  abstract void send (String recipient, String event);
}
