package behavioral.command.bad;

// Операция как набор разрозненных вызовов.
// Нельзя поставить в очередь, залогировать целиком, повторить при сбое или отменить.
final class BadTransferService {
    void transfer(String from, String to, long amount) {
        debit(from, amount);
        credit(to, amount);
        // если упали после debit — нет объекта, описывающего операцию,
        // нечего ретраить / откатывать
    }

    private void debit(String account, long amount) {
        System.out.println("debit  " + account + "  -" + amount);
    }

    private void credit(String account, long amount) {
        System.out.println("credit " + account + " +" + amount);
    }
}
