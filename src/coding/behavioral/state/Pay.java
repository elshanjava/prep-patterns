package coding.behavioral.state;

public class Pay implements OrderState {
    @Override
    public OrderState pay() {
        return illegal("pay");
    }

    @Override
    public OrderState ship() {
        return new Shipping();
    }

    @Override
    public OrderState deliver() {
        return illegal("deliver");
    }
}
