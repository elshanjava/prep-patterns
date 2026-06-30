package coding.behavioral.state;

public final class Order {
    private OrderState orderState = new Create();

    public void pay() {
        this.orderState = orderState.pay();
    }

    public void ship() {
        this.orderState = orderState.ship();
    }

    public void deliver() {
        this.orderState = orderState.deliver();
    }

    public String getStatus() {
        return orderState.getClass().getSimpleName();
    }
}
