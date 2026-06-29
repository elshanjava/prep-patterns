package coding.behavioral.observer;

// ConcreteObserver: резервирует/освобождает товар на складе.
public class WarehouseListener implements OrderListener {
    @Override
    public void onStatusChanged(Order o) {
        System.out.println("warehouse: заказ " + o.getId() + " теперь '" + o.getStatus() + "' -> обновляю резерв");
    }
}
