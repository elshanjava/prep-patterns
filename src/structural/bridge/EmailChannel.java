package structural.bridge;

final class EmailChannel implements MessageChannel {
  public void send(String to, String b){ System.out.println("  [email -> " + to + "] " + b); }
}