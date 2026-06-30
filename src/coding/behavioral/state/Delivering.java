package coding.behavioral.state;

public class Delivering implements OrderState {
    @Override
    public OrderState pay() {
        return illegal("pay");
    }

    @Override
    public OrderState ship() {
        return illegal("ship");
    }

    @Override
    public OrderState deliver() {
        return illegal("deliver");
    }
}
