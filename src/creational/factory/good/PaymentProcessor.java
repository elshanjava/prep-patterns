package creational.factory.good;

// Единый контракт: процессинг, комиссия и валидация живут в одном месте.
// Компилятор гарантирует: каждый новый метод оплаты реализует все три операции.
// Добавить APPLE_PAY и забыть о fee()? — ошибка компиляции, не баг в проде.
interface PaymentProcessor {
    void     validate(long amountCents);
    long     fee(long amountCents);
    void     process(long amountCents);
}
