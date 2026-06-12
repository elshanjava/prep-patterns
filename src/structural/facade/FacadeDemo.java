package structural.facade;

import java.util.Currency;

// Запуск: один вызов controller.pay() прячет оркестрацию пяти подсистем
// (антифрод -> курс -> авторизация -> проводка -> уведомление).
public class FacadeDemo {
    public static void main(String[] args) {
        System.out.println("== Facade ==");

        PaymentFacade facade = new PaymentFacade(
                new FraudCheck(), new FxService(), new PspClient(),
                new Ledger(), new Notifier());
        PaymentController controller = new PaymentController(facade);

        Order order = new Order("cust-1", Currency.getInstance("USD"), 4999);
        PaymentResult result = controller.pay(order);
        System.out.println("result: " + result);
    }
}
