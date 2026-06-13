package structural.decorator.bad;

public class BadDecoratorDemo {
    public static void main(String[] args) {
        System.out.println("== Decorator [BAD] — наследование вместо композиции ==");
        System.out.println();
        System.out.println("3 независимых поведения: Cache (C), Logging (L), RateLimit (R)");
        System.out.println("2^3 = 8 субклассов для всех комбинаций:");
        System.out.println("  1. BadDbRepository                        (none)");
        System.out.println("  2. BadCachingDbRepository                 (C)");
        System.out.println("  3. BadLoggingDbRepository                 (L)");
        System.out.println("  4. BadRateLimitingDbRepository            (R)");
        System.out.println("  5. BadLoggingCachingDbRepository          (L+C)");
        System.out.println("  6. BadRateLimitingCachingDbRepository     (R+C)");
        System.out.println("  7. BadRateLimitingLoggingDbRepository     (R+L)  ← ещё не написан");
        System.out.println("  8. BadRateLimitingLoggingCachingDbRepository (R+L+C) ← ещё не написан");
        System.out.println();

        System.out.println("--- Prod (cache only) ---");
        var prod = new BadCachingDbRepository();
        System.out.println("  1st: " + prod.find("42"));
        System.out.println("  2nd: " + prod.find("42"));   // из кэша

        System.out.println("--- Staging (log + cache) ---");
        var staging = new BadLoggingCachingDbRepository();
        staging.find("99");
        staging.find("99");

        System.out.println("--- Rate-limited (rate + cache) ---");
        var limited = new BadRateLimitingCachingDbRepository(2);
        limited.find("1");
        limited.find("2");
        try { limited.find("3"); } catch (RuntimeException e) { System.out.println("  " + e.getMessage()); }

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  Хочешь RateLimit + Log без Cache? → новый BadRateLimitingLoggingDbRepository");
        System.out.println("  Поменять базу на Cassandra? → переписывай ВСЕ 8 подклассов");
        System.out.println("  Добавить Circuit Breaker (4-е поведение) → 2^4 = 16 субклассов");
        System.out.println("  Порядок применения (сначала rate-limit, потом cache) жёстко зашит в иерархии");
    }
}
