package coding.structural.proxy;

public class Main {

  public static void main(String[] args) {
    // Передаём ФАБРИКУ (RealImageLoader::new), а не готовый объект —
    // реальный загрузчик ещё НЕ создан.
    System.out.println("Создаём прокси (реальный RealImageLoader пока не существует)");
    ImageLoader loader = new CachingLazyProxy(RealImageLoader::new);
    System.out.println();

    System.out.println("1-й load(\"photo.png\") → ленивая инициализация + реальная загрузка:");
    loader.load("photo.png");
    System.out.println();

    System.out.println("2-й load(\"photo.png\") → из кэша, без загрузки:");
    loader.load("photo.png");
    System.out.println();

    System.out.println("load(\"avatar.jpg\") → новый путь: реальная загрузка");
    System.out.println("(но повторной ленивой инициализации НЕТ — объект уже создан):");
    loader.load("avatar.jpg");
  }
}
