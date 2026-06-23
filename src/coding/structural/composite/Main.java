package coding.structural.composite;

public class Main {

  public static void main(String[] args) {
    Folder root = new Folder("root");
    root.add(new File("a.txt", 100));

    Folder sub = new Folder("sub");
    sub.add(new File("b.txt", 30));
    sub.add(new File("c.txt", 20));

    root.add(sub);  // в папку кладём другую папку — вложенность

    // Клиент зовёт size() одинаково у File и Folder, не различая их.
    System.out.println("a.txt size = " + new File("a.txt", 100).size());
    System.out.println("sub size   = " + sub.size());   // 30 + 20 = 50
    System.out.println("root size  = " + root.size());  // 100 + 50 = 150 (рекурсия зашла в sub)
  }
}
