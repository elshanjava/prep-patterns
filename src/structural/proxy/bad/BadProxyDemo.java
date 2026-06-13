package structural.proxy.bad;

public class BadProxyDemo {
    public static void main(String[] args) {
        System.out.println("== Proxy [BAD] — логика склеена в одном классе ==");

        var service = new BadAccountService();

        System.out.println("1st load:");
        System.out.println("  " + service.get("42"));

        System.out.println("2nd load (cache hit):");
        System.out.println("  " + service.get("42"));

        try {
            service.get("secret-1");
        } catch (SecurityException e) {
            System.out.println("  blocked: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - unit-тест DB-загрузки тянет за собой security + cache");
        System.out.println("  - в интеграционных тестах нельзя отключить кэш без флагов");
        System.out.println("  - добавить rate-limiting → правь этот же класс (нарушение SRP)");
    }
}
