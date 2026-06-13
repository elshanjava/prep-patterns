package structural.proxy.bad;

public class BadProxyDemo {
    public static void main(String[] args) {
        System.out.println("== Proxy [BAD] — security + rate-limit + кэш в одном классе ==");
        System.out.println();

        var service = new BadAccountService();

        System.out.println("1st load:");
        System.out.println("  " + service.get("42"));

        System.out.println("2nd load (cache hit):");
        System.out.println("  " + service.get("42"));

        System.out.println("Security check:");
        try {
            service.get("secret-1");
        } catch (SecurityException e) {
            System.out.println("  blocked: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  3 сквозных заботы (security + rate-limit + cache) слиты в 1 класс");
        System.out.println("  unit-тест DB-загрузки: нужно настраивать security + rate-limit + кэш");
        System.out.println("  в интеграционных тестах нельзя отключить кэш без флагов");
        System.out.println("  добавить circuit-breaker: правь этот класс → он уже нарушает SRP трижды");
        System.out.println("  order matters: rate-limit ДО кэша или ПОСЛЕ? зашито, не конфигурируется");
    }
}
