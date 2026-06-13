package behavioral.observer.bad;

final class RiskService {
    void evaluate(String orderId, long amount) {
        System.out.println("risk: evaluated " + orderId + " amount=" + amount);
    }
}
