package coding.structural.bridge;

public class PushChannel implements Channel{

  @Override
  public void send(String msg) {
    System.out.println("Send message: " + msg + " via push channel");
  }
}
