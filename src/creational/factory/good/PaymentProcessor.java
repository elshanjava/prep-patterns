package creational.factory.good;

// Единый контракт для всех способов оплаты.
// Клиент работает только с этим интерфейсом — конкретные классы скрыты в фабрике.
interface PaymentProcessor {
    void process(long amountCents);
}
