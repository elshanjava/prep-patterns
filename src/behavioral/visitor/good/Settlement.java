package behavioral.visitor.good;

import java.math.BigDecimal;

// Новый тип узла: достаточно реализовать accept() — посетители не меняются,
// если они уже обрабатывают Settlement через интерфейс TxVisitor.
// Добавление Settlement потребовало: 1) этот класс, 2) метод visit(Settlement) в TxVisitor,
// 3) реализацию в каждом существующем Visitor — компилятор показывает все места.
// В bad/: нужно было вставить instanceof-ветку в amount() И taxAmount() вручную.
final class Settlement implements TxNode {
    final BigDecimal net;      // чистая сумма после взаиморасчёта
    final String     partner;  // банк-партнёр

    Settlement(BigDecimal net, String partner) {
        this.net     = net;
        this.partner = partner;
    }

    public <R> R accept(TxVisitor<R> v) { return v.visit(this); }
}
