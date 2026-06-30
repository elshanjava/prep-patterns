package coding.behavioral.state;

public class Main {

    public static void main(String[] args) {
        Order order = new Order();
        System.out.println("старт: " + order.getStatus());   // Create

        // --- легальный путь: Create -> Pay -> Shipping -> Delivering ---
        System.out.println();
        System.out.println("-- легальный путь --");
        order.pay();
        System.out.println("после pay():     " + order.getStatus());   // Pay
        order.ship();
        System.out.println("после ship():    " + order.getStatus());   // Shipping
        order.deliver();
        System.out.println("после deliver(): " + order.getStatus());   // Delivering

        // --- нелегальный переход: из свежего заказа сразу deliver() ---
        System.out.println();
        System.out.println("-- нелегальный переход --");
        Order fresh = new Order();   // снова Create
        try {
            fresh.deliver();          // из Create доставить нельзя
        } catch (IllegalStateException e) {
            System.out.println("deliver() из Create -> " + e.getMessage());
        }

        // --- ещё один: повторная оплата уже оплаченного ---
        Order paid = new Order();
        paid.pay();                   // Create -> Pay
        try {
            paid.pay();               // Pay -> pay() запрещён
        } catch (IllegalStateException e) {
            System.out.println("pay() из Pay     -> " + e.getMessage());
        }
    }
}
