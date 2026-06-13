package structural.decorator.bad;

import structural.decorator.model.Account;

// Базовый репозиторий без паттерна.
class BadDbRepository {
    public Account find(String id) {
        System.out.println("  [DB] SELECT account " + id);
        return new Account(id, "Account#" + id);
    }
}
