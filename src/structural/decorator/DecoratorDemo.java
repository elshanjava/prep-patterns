package structural.decorator;

// Запуск: слои оборачивают друг друга в рантайме. Logging -> Caching -> Db.
// Первый вызов проходит сквозь все слои (промах кэша, поход в БД);
// второй — лог есть, но БД уже не дёргается (попадание в кэш).
public class DecoratorDemo {
    public static void main(String[] args) {
        System.out.println("== Decorator ==");
        Repository repo = new LoggingRepository(new CachingRepository(new DbRepository()));

        System.out.println("1st: " + repo.find("42"));   // LOG + промах кэша + DB
        System.out.println("2nd: " + repo.find("42"));   // LOG + из кэша, DB молчит
    }
}
