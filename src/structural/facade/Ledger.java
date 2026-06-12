package structural.facade;

// Подсистема №4: бухгалтерская книга (проводки).
final class Ledger {
    void record(Authorization auth) {
        System.out.println("  [ledger] recorded " + auth.id());
    }
}
