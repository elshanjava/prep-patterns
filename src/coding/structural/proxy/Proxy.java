package coding.structural.proxy;

import java.util.function.Supplier;

public abstract class Proxy implements ImageLoader {

  // Не готовый объект, а фабрика — чтобы создать реальный загрузчик ПОЗЖЕ, по требованию.
  private final Supplier<ImageLoader> factory;
  private ImageLoader real;   // реальный объект; null, пока его впервые не запросят

  protected Proxy(Supplier<ImageLoader> factory) {
    this.factory = factory;
  }

  // Ленивый доступ (virtual proxy): реальный ImageLoader не существует,
  // пока его не позвали впервые. Создаётся ровно один раз.
  protected ImageLoader loader() {
    if (real == null) {
      System.out.println("  [lazy] создаю RealImageLoader (первое обращение)");
      real = factory.get();
    }
    return real;
  }
}
