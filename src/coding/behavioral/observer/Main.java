package coding.behavioral.observer;

public class Main {

    public static void main(String[] args) {
        OrderPublisher publisher = new OrderPublisher();
        OrderService service = new OrderService(publisher);

        // подписываем слушателей — OrderService про них не знает
        EmailListener email = new EmailListener();
        WarehouseListener warehouse = new WarehouseListener();
        publisher.subscribe(email);
        publisher.subscribe(warehouse);

        // лямбда вместо отдельного класса (OrderListener — функциональный интерфейс)
        publisher.subscribe(o ->
                System.out.println("analytics: записал переход заказа " + o.getId() + " в '" + o.getStatus() + "'"));

        Order order = new Order("ord-1", "NEW");

        System.out.println("-- смена статуса: PAID (слушают все 3) --");
        service.publishOrderStatus(order, "PAID");

        System.out.println();
        System.out.println("-- отписываем warehouse --");
        publisher.unsubscribe(warehouse);

        System.out.println("-- смена статуса: SHIPPED (warehouse уже не получит) --");
        service.publishOrderStatus(order, "SHIPPED");
    }
}
