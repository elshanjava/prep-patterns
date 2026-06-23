package coding.structural.bridge;

public class Urgent extends NotificationType{

  Urgent(Channel channel) {
    super(channel);
  }

  @Override
  void send(String recipient, String event) {
    channel.send("Send urgent event: " + event + "from: " + recipient);
  }
}
