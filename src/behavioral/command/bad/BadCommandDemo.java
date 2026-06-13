package behavioral.command.bad;

import java.util.List;

public class BadCommandDemo {
    public static void main(String[] args) {
        System.out.println("== Command [BAD] ==");

        var service = new BadTransferService();

        // --- одиночный перевод ---
        System.out.println("-- одиночный перевод --");
        service.transfer("acc-A", "acc-B", 500);
        // операцию нельзя: поставить в очередь, повторить, залогировать, откатить

        // --- батч-перевод: 2-й перевод провалится (amount=999 = триггер сбоя) ---
        System.out.println();
        System.out.println("-- батч: 3 перевода, 2-й завершится сбоем credit --");
        try {
            service.transferBatch(List.of(
                    new long[]{0, 1, 100},   // acc-A → acc-B: OK
                    new long[]{1, 2, 999},   // acc-B → acc-C: credit упадёт!
                    new long[]{2, 3, 200}    // acc-C → acc-D: никогда не дойдёт
            ));
        } catch (RuntimeException e) {
            System.out.println("ОШИБКА: " + e.getMessage());
            // Что успело выполниться? Неизвестно.
            // debit acc-A и acc-B прошли. credit acc-B прошёл. credit acc-C — нет.
            // Retry всего батча продублирует уже выполненный перевод acc-A → acc-B.
        }

        // --- журнал аудита пуст ---
        System.out.println();
        System.out.println("-- журнал аудита --");
        System.out.println("audit log: " + service.getAuditLog());
        // Вывод: [] — невозможно восстановить, что выполнялось

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - батч провалился: неизвестно, какие переводы успели → partial state");
        System.out.println("  - retry батча → дублирование успешных переводов");
        System.out.println("  - аудит пуст: операции нигде не зафиксированы как объекты");
        System.out.println("  - undo невозможен: нет объекта, хранящего from/to/amount");
    }
}
