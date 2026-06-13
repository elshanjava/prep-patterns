package structural.proxy.bad;

import structural.proxy.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Security-проверка и кэш вмешаны прямо в бизнес-сервис.
// Нельзя протестировать загрузку из БД без security-логики.
// Нельзя протестировать security без кэша.
// Хочешь убрать кэш в тестах? — нельзя, он зашит внутри.
// Добавить rate-limiting? — правь этот же класс.
final class BadAccountService {
    private final Map<String, Account> cache = new ConcurrentHashMap<>();

    public Account get(String id) {
        // security вперемешку с бизнес-логикой
        if (id.startsWith("secret")) {
            throw new SecurityException("access denied: " + id);
        }

        // кэш вперемешку с загрузкой
        if (cache.containsKey(id)) {
            System.out.println("  [cache] hit: " + id);
            return cache.get(id);
        }

        // реальная загрузка
        System.out.println("  [store] loading account " + id);
        Account account = new Account(id, 100_00L);

        cache.put(id, account);
        return account;
    }
    // Протестировать только loadFromStore()? Нельзя — она приватная и смешана с остальным.
    // Нужен сервис без кэша (для тестов)? — дублируй класс или добавляй флаги.
}
