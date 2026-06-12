package structural.facade;

public final class PaymentFacade {
    private final FraudCheck fraudCheck;
    private final FxService fxService;
    private final PspClient pspClient;
    private final Ledger ledger;
    private final Notifier notifier;

    // DI всех зависимостей-подсистем: фасад их оркестрирует, но не владеет логикой.
    public PaymentFacade(FraudCheck fraudCheck, FxService fxService, PspClient pspClient,
                         Ledger ledger, Notifier notifier) {
        this.fraudCheck = fraudCheck;
        this.fxService = fxService;
        this.pspClient = pspClient;
        this.ledger = ledger;
        this.notifier = notifier;
    }

    // Клиенту видна ОДНА операция вместо пяти сервисов
    public PaymentResult pay(Order order) {
        fraudCheck.validate(order);
        var rate   = fxService.rate(order.currency());
        var auth   = pspClient.authorize(order, rate);
        ledger.record(auth);
        notifier.send(order.customer(), auth);
        return PaymentResult.of(auth);
    }
}
