package structural.decorator.bad;

public class BadDecoratorDemo {
    public static void main(String[] args) {
        System.out.println("== Decorator [BAD] — наследование вместо композиции ==");

        var repo = new BadLoggingCachingDbRepository();

        System.out.println("1st call:");
        System.out.println("  " + repo.find("42"));

        System.out.println("2nd call (from cache):");
        System.out.println("  " + repo.find("42"));

        System.out.println();
        System.out.println("Хочешь только кэш без logging? → отдельный BadCachingDbRepository.");
        System.out.println("Хочешь logging без кэша?       → BadLoggingDbRepository (новый класс).");
        System.out.println("Хочешь ещё rate-limiting?      → ещё один уровень наследования.");
        System.out.println("Хочешь NoSQL вместо DB?        → переписывай ВСЮ иерархию.");
    }
}
