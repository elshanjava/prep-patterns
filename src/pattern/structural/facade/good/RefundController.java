package pattern.structural.facade.good;

import pattern.structural.facade.model.PaymentResult;

final class RefundController {
    private final PaymentFacade facade = new PaymentFacade();

    PaymentResult refund(String customer, String authId, long amountCents) {
        return facade.refund(customer, authId, amountCents);
    }
}
