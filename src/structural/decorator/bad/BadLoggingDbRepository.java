package structural.decorator.bad;

import structural.decorator.model.Account;

// Логирование без кэша — отдельный класс (комбинаций становится больше).
// Нельзя просто «включить» логирование у BadCachingDbRepository без нового подкласса.
class BadLoggingDbRepository extends BadDbRepository {
    @Override
    public Account find(String id) {
        System.out.println("  [LOG] find(" + id + ")");
        long t0     = System.nanoTime();
        Account acc = super.find(id);
        System.out.printf("  [LOG] find(%s) %d µs%n", id, (System.nanoTime() - t0) / 1_000);
        return acc;
    }
}
