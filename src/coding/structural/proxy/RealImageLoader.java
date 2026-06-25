package coding.structural.proxy;

public class RealImageLoader implements ImageLoader {

  @Override
  public void load(String payload) {
    System.out.println("Load data: " + payload);
  }
}
