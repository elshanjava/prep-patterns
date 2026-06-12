package structural.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Прокси того же типа, что и реальный сервис: добавляет контроль доступа и
// ленивый кэш, не меняя интерфейс для клиента.
final class SecuredCachingProxy implements AccountService {
    private final AccountService target;             // реальный объект
    private final Map<String, Account> cache = new ConcurrentHashMap<>();
    SecuredCachingProxy(AccountService target) { this.target = target; }
 
    public Account get(String id) {
        if (!SecurityContext.canRead(id))            // контроль доступа ДО target
            throw new SecurityException("access denied: " + id);
        return cache.computeIfAbsent(id, target::get);  // ленивый кэш поверх target
    }
}
// AccountService svc = new SecuredCachingProxy(new RealAccountService());
