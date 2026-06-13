package structural.proxy.good;

public class ProxyDemo {
    public static void main(String[] args) {
        System.out.println("== Proxy [GOOD] — слои разделены, каждый тестируется отдельно ==");

        AccountService service = new SecuredCachingProxy(
                new RealAccountService(),
                new SecurityContext());

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
        System.out.println("Преимущества над bad:");
        System.out.println("  - RealAccountService тестируется без ACL и кэша");
        System.out.println("  - SecuredCachingProxy тестируется с mock-делегатом");
        System.out.println("  - добавить rate-limiting: RateLimitProxy оборачивает снаружи — 0 правок в существующем коде");
        System.out.println("  - в Spring: @Cacheable/@PreAuthorize — тот же паттерн через AOP-прокси");
    }
}
