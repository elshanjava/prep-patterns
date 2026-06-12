package structural.proxy;

// Реальный субъект: дорогая загрузка из хранилища. Прокси оборачивает именно его.
final class RealAccountService implements AccountService {
    public Account get(String id) {
        System.out.println("  [store] loading account " + id);   // дорогая операция
        return new Account(id, 100_00);
    }
}
