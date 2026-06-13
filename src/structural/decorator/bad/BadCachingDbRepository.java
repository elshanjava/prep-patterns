package structural.decorator.bad;

import structural.decorator.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Решение через наследование: CachingDbRepository жёстко привязан к BadDbRepository.
// Нельзя подменить «базовый» репозиторий (например, на NoSQL) без переписывания.
// Нельзь добавить logging независимо — нужен ещё один подкласс.
class BadCachingDbRepository extends BadDbRepository {
    private final Map<String, Account> cache = new ConcurrentHashMap<>();

    @Override
    public Account find(String id) {
        return cache.computeIfAbsent(id, super::find);
    }
}
