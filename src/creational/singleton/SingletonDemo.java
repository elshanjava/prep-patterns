package creational.singleton;

// Запуск: все три реализации возвращают один и тот же экземпляр.
//   Config  — holder idiom (ленивый, потокобезопасный без synchronized)
//   Config2 — enum (самый простой и защищённый от рефлексии/сериализации)
//   Config3 — double-checked locking (volatile обязателен)
public class SingletonDemo {
    public static void main(String[] args) {
        System.out.println("== Singleton ==");
        System.out.println("Holder idiom: " + (Config.getInstance()  == Config.getInstance()));
        System.out.println("Enum:         " + (Config2.INSTANCE       == Config2.INSTANCE));
        System.out.println("DCL:          " + (Config3.getInstance()  == Config3.getInstance()));
    }
}
