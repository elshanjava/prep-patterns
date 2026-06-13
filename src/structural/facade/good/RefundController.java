package structural.facade.good;

import structural.facade.model.PaymentResult;

final class RefundController {
    private final PaymentFacade facade = new PaymentFacade();

    PaymentResult refund(String customer, String authId, long amountCents) {
        return facade.refund(customer, authId, amountCents);
    }
}
