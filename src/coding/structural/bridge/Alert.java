package coding.structural.bridge;

public class Alert extends NotificationType{

  Alert(Channel channel) {
    super(channel);
  }

  @Override
  void send(String recipient, String event) {
    channel.send("Send alert notification: " + event + "from: " + recipient);

  }
}
