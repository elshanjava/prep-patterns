package structural.proxy;

// Запуск: клиент держит интерфейс AccountService и не знает, что за ним прокси.
// Прокси добавляет два аспекта поверх реального сервиса: кэш и контроль доступа.
public class ProxyDemo {
    public static void main(String[] args) {
        System.out.println("== Proxy ==");
        AccountService svc = new SecuredCachingProxy(new RealAccountService());

        System.out.println("1st: " + svc.get("42"));   // идём в target
        System.out.println("2nd: " + svc.get("42"));   // из кэша, target молчит

        try {
            svc.get("secret-1");                        // доступ запрещён
        } catch (SecurityException e) {
            System.out.println("blocked: " + e.getMessage());
        }
    }
}
