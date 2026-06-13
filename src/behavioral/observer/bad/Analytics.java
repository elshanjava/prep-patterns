package behavioral.observer.bad;

final class Analytics {
    void track(String orderId, long amount) {
        System.out.println("analytics: tracked " + orderId);
    }
}
