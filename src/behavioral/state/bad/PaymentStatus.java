package behavioral.state.bad;

// Добавление CAPTURED = правь ВСЕ switch-блоки в BadPayment: capture(), refund(), cancel()
enum PaymentStatus { PENDING, AUTHORIZED, CAPTURED, REFUNDED }
