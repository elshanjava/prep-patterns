package pattern.structural.decorator.good;

import pattern.structural.decorator.model.Account;

// Только загрузка из БД — никакого кэша, никакого логирования.
// Тестируется в полной изоляции.
final class DbRepository implements Repository {
    @Override
    public Account find(String id) {
        System.out.println("  [DB] SELECT account " + id);
        return new Account(id, "Account#" + id);
    }
}
