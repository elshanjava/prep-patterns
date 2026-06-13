package structural.proxy.good;

public class ProxyDemo {
    public static void main(String[] args) {
        System.out.println("== Proxy [GOOD] — 3 независимых слоя, каждый тестируется отдельно ==");
        System.out.println();

        // PROD стек: Security → RateLimit → Cache → Real
        AccountService prod = new SecuredCachingProxy(
                                  new RateLimitingProxy(
                                      new SecuredCachingProxy(  // внутренний кэш перед DB
                                          new RealAccountService(),
                                          new SecurityContext()),
                                  100),
                              new SecurityContext());

        // Упрощённый стек для демонстрации: Security → Cache → Real
        AccountService service = new SecuredCachingProxy(
                new RateLimitingProxy(
                        new RealAccountService(), 3),
                new SecurityContext());

        System.out.println("--- Нормальная работа ---");
        System.out.println("  " + service.get("42"));
        System.out.println("  " + service.get("42"));  // cache hit

        System.out.println();
        System.out.println("--- Security check ---");
        try { service.get("secret-1"); } catch (SecurityException e) {
            System.out.println("  security blocked: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- Rate limit ---");
        AccountService limited = new RateLimitingProxy(new RealAccountService(), 2);
        limited.get("x");
        limited.get("y");
        try { limited.get("z"); } catch (RuntimeException e) {
            System.out.println("  rate-limit: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- TEST: только DB, без прокси ---");
        AccountService rawDb = new RealAccountService();
        rawDb.get("test-account");  // security и rate-limit не мешают тесту

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  RealAccountService тестируется без ACL, кэша и rate-limit");
        System.out.println("  SecuredCachingProxy тестируется с mock-делегатом");
        System.out.println("  RateLimitingProxy тестируется независимо — с любым порогом");
        System.out.println("  порядок слоёв гибкий: Security→Rate→Cache или Rate→Security→Cache");
        System.out.println("  добавить circuit breaker: CircuitBreakerProxy, оборачиваем снаружи — 0 правок");
        System.out.println("  в Spring: @PreAuthorize + @Cacheable + @RateLimiter — тот же паттерн через AOP");
    }
}
