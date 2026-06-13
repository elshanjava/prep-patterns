package structural.proxy.bad;

import structural.proxy.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Security-проверка, кэш И rate-limiting вмешаны прямо в бизнес-сервис.
// 3 независимых сквозных заботы (cross-cutting concerns) в одном классе.
// Протестировать только DB-загрузку: нельзя — security и rate-limit всегда срабатывают.
// Убрать кэш в интеграционных тестах: нельзя — он зашит внутри.
// Добавить circuit breaker: правь этот же класс (SRP нарушен трижды).
final class BadAccountService {
    private final Map<String, Account> cache   = new ConcurrentHashMap<>();
    private final AtomicInteger        counter = new AtomicInteger();
    private static final int           MAX_RPS = 100;

    public Account get(String id) {
        // concern #1: security — вперемешку с бизнес-логикой
        if (id.startsWith("secret")) {
            throw new SecurityException("access denied: " + id);
        }

        // concern #2: rate-limiting — тоже здесь
        if (counter.incrementAndGet() > MAX_RPS) {
            throw new RuntimeException("rate limit exceeded (" + MAX_RPS + " rps)");
        }

        // concern #3: кэш — тоже здесь
        if (cache.containsKey(id)) {
            System.out.println("  [cache] hit: " + id);
            return cache.get(id);
        }

        // собственно загрузка
        System.out.println("  [store] loading account " + id);
        Account account = new Account(id, 100_00L);
        cache.put(id, account);
        return account;
    }
    // Хочешь тестировать loadFromStore() без security? Нельзя — он приватный и смешан.
    // Хочешь тестировать кэш с mock-store? Нельзя — store создаётся неявно внутри.
    // Нужен другой rate-limit в тестах? Флаг? Подкласс? Всё это костыли.
}
