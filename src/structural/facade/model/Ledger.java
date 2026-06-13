package structural.facade.model;

public final class Ledger {
    public void record(Authorization auth) {
        System.out.println("  [ledger] recorded " + auth.id());
    }
}
