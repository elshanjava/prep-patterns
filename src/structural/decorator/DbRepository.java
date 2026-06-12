package structural.decorator;

// Базовый («настоящий») компонент — он реально ходит в БД.
final class DbRepository implements Repository {
    public Account find(String id) {
        System.out.println("  [DB] SELECT account " + id);   // дорогой поход в БД
        return new Account(id, "Account#" + id);
    }
}
