package structural.proxy.good;

import structural.proxy.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Прокси: добавляет security-проверку и кэш, не меняя RealAccountService.
// Каждый слой тестируется отдельно:
//   - RealAccountService — только DB-загрузка
//   - SecuredCachingProxy — только ACL + кэш, с mock AccountService
final class SecuredCachingProxy implements AccountService {
    private final AccountService       delegate;
    private final SecurityContext      security;
    private final Map<String, Account> cache = new ConcurrentHashMap<>();

    SecuredCachingProxy(AccountService delegate, SecurityContext security) {
        this.delegate = delegate;
        this.security = security;
    }

    @Override
    public Account get(String id) {
        if (!security.isAllowed(id)) {
            throw new SecurityException("access denied: " + id);
        }
        return cache.computeIfAbsent(id, key -> {
            System.out.println("  [cache] miss: " + key);
            return delegate.get(key);
        });
    }
}
