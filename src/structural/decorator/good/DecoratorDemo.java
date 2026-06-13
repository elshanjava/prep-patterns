package structural.decorator.good;

public class DecoratorDemo {
    public static void main(String[] args) {
        System.out.println("== Decorator [GOOD] — 3 декоратора вместо 8 субклассов ==");
        System.out.println();
        System.out.println("Те же 3 поведения: Cache (C), Logging (L), RateLimit (R)");
        System.out.println("Классов: 4 (DbRepository + 3 декоратора) вместо 8 субклассов.");
        System.out.println("Комбинации задаются в runtime — не в compile time.");
        System.out.println();

        System.out.println("--- PROD: RateLimit → Cache → DB ---");
        Repository prod = new RateLimitingRepository(
                              new CachingRepository(
                                  new DbRepository()), 100);
        System.out.println("  1st: " + prod.find("42"));
        System.out.println("  2nd: " + prod.find("42"));  // cache hit, не идёт в DB

        System.out.println();
        System.out.println("--- STAGING: Log → Cache → DB (тот же стек, без rate-limit) ---");
        Repository staging = new LoggingRepository(
                                 new CachingRepository(
                                     new DbRepository()));
        staging.find("99");
        staging.find("99");

        System.out.println();
        System.out.println("--- TEST: только DB, без декораторов ---");
        Repository test = new DbRepository();
        test.find("777");

        System.out.println();
        System.out.println("--- Rate-limit hit ---");
        Repository limited = new RateLimitingRepository(new DbRepository(), 1);
        limited.find("x");
        try { limited.find("y"); } catch (RuntimeException e) { System.out.println("  " + e.getMessage()); }

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  3 декоратора покрывают 8 комбинаций — без дополнительных классов");
        System.out.println("  Добавить Circuit Breaker: 1 класс CircuitBreakerRepository — не 8 новых");
        System.out.println("  Поменять базу на Cassandra: меняешь DbRepository, всё остальное не трогаешь");
        System.out.println("  Порядок декораторов гибкий: RateLimit→Cache или Cache→RateLimit — в одну строку");
        System.out.println("  В Spring: @Cacheable + @RateLimiter — тот же паттерн через AOP");
    }
}
