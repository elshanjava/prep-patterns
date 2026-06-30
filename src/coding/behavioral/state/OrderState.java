package coding.behavioral.state;

public interface OrderState {
    OrderState pay();
    OrderState ship();
    OrderState deliver();

    default OrderState illegal(String reason) {
        throw new IllegalStateException("Decline reason: " + reason);
    }
}
