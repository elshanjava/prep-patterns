package behavioral.command.bad;

import java.util.List;

// Операция как набор разрозненных вызовов — нет объекта, описывающего её целиком.
// Нельзя поставить в очередь, залогировать, повторить при сбое или откатить.
final class BadTransferService {

    void transfer(String from, String to, long amount) {
        debit(from, amount);
        credit(to, amount);
        // Если credit упал — debit уже прошёл. Нет объекта операции → нечего ретраить.
        // Нечего откатывать, нечего отправить в dead-letter queue.
    }

    // Батч-перевод: если 2-й credit провалился — непонятно, что успело выполниться.
    // Caller получает исключение, но не знает, какие переводы прошли, а какие нет.
    // Retry всего батча приведёт к дублям успешных переводов.
    void transferBatch(List<long[]> transfers) {
        // transfers: каждый элемент — [fromIndex, toIndex, amount] — совсем примитивно
        String[] accounts = {"acc-A", "acc-B", "acc-C", "acc-D"};
        for (long[] t : transfers) {
            String from   = accounts[(int) t[0]];
            String to     = accounts[(int) t[1]];
            long   amount = t[2];
            debit(from, amount);
            credit(to, amount);
            // Если credit бросил исключение — цикл прерывается.
            // Предыдущие debit/credit выполнены, текущий debit выполнен, credit — нет.
            // Вызывающий код не может отличить "что прошло" от "что нет".
        }
    }

    // Журнал аудита всегда пуст — операции нигде не регистрируются как объекты.
    List<String> getAuditLog() {
        return List.of(); // невозможно вернуть что-то осмысленное — операций-объектов нет
    }

    private void debit(String account, long amount) {
        System.out.println("debit  " + account + "  -" + amount);
    }

    private void credit(String account, long amount) {
        // Симуляция сбоя на 3-м вызове — в реальности это сетевой таймаут или ошибка БД
        if (amount == 999) throw new RuntimeException("credit failed: timeout");
        System.out.println("credit " + account + " +" + amount);
    }
}
