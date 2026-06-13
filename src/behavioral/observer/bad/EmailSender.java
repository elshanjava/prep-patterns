package behavioral.observer.bad;

final class EmailSender {
    void sendReceipt(String orderId, long amount) {
        System.out.println("email: receipt for " + orderId + ", amount=" + amount);
    }
}
