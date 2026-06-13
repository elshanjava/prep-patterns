package structural.decorator.good;

import structural.decorator.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class CachingRepository extends RepositoryDecorator {
    private final Map<String, Account> cache = new ConcurrentHashMap<>();

    CachingRepository(Repository delegate) { super(delegate); }

    @Override
    public Account find(String id) {
        return cache.computeIfAbsent(id, key -> {
            System.out.println("  [cache] miss: " + key);
            return delegate.find(key);
        });
    }
}
