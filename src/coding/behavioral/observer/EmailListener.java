package coding.behavioral.observer;

// ConcreteObserver: шлёт письмо клиенту при смене статуса.
public class EmailListener implements OrderListener {
    @Override
    public void onStatusChanged(Order o) {
        System.out.println("email: заказу " + o.getId() + " -> письмо 'статус: " + o.getStatus() + "'");
    }
}
