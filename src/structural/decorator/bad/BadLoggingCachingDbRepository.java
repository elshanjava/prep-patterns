package structural.decorator.bad;

import structural.decorator.model.Account;

// Ещё один подкласс чтобы добавить logging поверх кэша.
// Нужен rate-limiting? → BadRateLimitingLoggingCachingDbRepository.
// Хочешь только logging без кэша? → BadLoggingDbRepository (новый класс).
// M декораторов = 2^M комбинаций подклассов — экспоненциальный взрыв.
final class BadLoggingCachingDbRepository extends BadCachingDbRepository {
    @Override
    public Account find(String id) {
        System.out.println("  [LOG] find " + id);
        return super.find(id);
    }
}
