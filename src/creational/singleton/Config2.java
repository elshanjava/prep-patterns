package creational.singleton;

public enum Config2 {
  INSTANCE;
  // методы и состояние как у обычного класса
  public void reload() { /* ... */ }

}
// Использование: Config2.INSTANCE.reload();
