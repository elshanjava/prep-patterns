package structural.decorator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Декоратор: тот же интерфейс Repository, добавляет кэш ВОКРУГ делегата.
final class CachingRepository extends RepositoryDecorator {
    private final Map<String, Account> cache = new ConcurrentHashMap<>();
    CachingRepository(Repository delegate) { super(delegate); }
    public Account find(String id) {
        return cache.computeIfAbsent(id, delegate::find);  // добавили поведение ВОКРУГ
    }
}
