package creational.singleton;

public final class Config3 {
  // volatile ОБЯЗАТЕЛЕН — без него паттерн сломан
  private static volatile Config3 instance;
  private Config3() {}
  public static Config3 getInstance() {
    if (instance == null) {                  // 1-я проверка без лока (быстрый путь)
      synchronized (Config3.class) {
        if (instance == null) {          // 2-я проверка под локом
          instance = new Config3();
        }
      }
    }
    return instance;
  }

}
