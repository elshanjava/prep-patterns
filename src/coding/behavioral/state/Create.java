package coding.behavioral.state;

public class Create implements OrderState {
    @Override
    public OrderState pay() {
        return new Pay();
    }

    @Override
    public OrderState ship() {
        return illegal("shipping");
    }

    @Override
    public OrderState deliver() {
        return illegal("deliver");
    }
}
