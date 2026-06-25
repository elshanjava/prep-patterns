package coding.behavioral.command;

public class Stereo {

  private int volume;

  void up() {
    volume++;
    System.out.println("Volume up → " + volume);
  }
  void down() {
    volume--;
    System.out.println("Volume down → " + volume);
  }
}
