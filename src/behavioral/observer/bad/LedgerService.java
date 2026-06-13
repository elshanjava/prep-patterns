package behavioral.observer.bad;

final class LedgerService {
    void record(String orderId, long amount) {
        System.out.println("ledger: recorded " + orderId);
    }
}
