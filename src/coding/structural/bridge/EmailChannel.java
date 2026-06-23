package coding.structural.bridge;

public class EmailChannel implements Channel{

  @Override
  public void send(String msg) {
    System.out.println("Send message: " + msg + "  via email channel");
  }
}
