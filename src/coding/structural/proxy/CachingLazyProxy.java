package coding.structural.proxy;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class CachingLazyProxy extends Proxy {

  private final Set<String> loaded = new HashSet<>();

  public CachingLazyProxy(Supplier<ImageLoader> factory) {
    super(factory);
  }

  @Override
  public void load(String payload) {
    if (loaded.contains(payload)) {
      System.out.println("cache hit: " + payload);   // второй раз — НЕ грузим
      return;
    }
    loader().load(payload);      // loader() лениво создаёт реальный объект при ПЕРВОМ обращении
    loaded.add(payload);         // запомнили
  }
}
