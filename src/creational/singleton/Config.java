package creational.singleton;

public final class Config {                 // final: запрет наследования
    private Config() {                       // приватный конструктор
        // тяжёлая инициализация: чтение конфигурации, прогрев кэша...
    }
 
    // Вложенный класс НЕ загружается, пока к нему не обратятся
    private static final class Holder {
        private static final Config INSTANCE = new Config();
    }
 
    public static Config getInstance() {
        return Holder.INSTANCE;              // первый вызов -> загрузка Holder -> создание
    }
}
