package behavioral.command.good;

import behavioral.command.model.AccountService;

public class CommandDemo {
    public static void main(String[] args) {
        System.out.println("== Command [GOOD] ==");

        // AccountService — реальный сервис; здесь заменён лямбдой для читаемости
        AccountService accounts = (from, to, amount, key) ->
                System.out.printf("transfer %d: %s → %s [key=%s]%n", amount, from, to, key);

        var bus = new CommandBus();

        // --- 1. Три команды: журнал фиксирует все ---
        System.out.println("-- 1. Подача 3 команд --");
        var cmd1 = new TransferCommand(accounts, "acc-A", "acc-B", 500,  "tx-001");
        var cmd2 = new TransferCommand(accounts, "acc-B", "acc-C", 1000, "tx-002");
        var cmd3 = new TransferCommand(accounts, "acc-C", "acc-D", 250,  "tx-003");

        bus.submit(cmd1);
        bus.submit(cmd2);
        bus.submit(cmd3);

        System.out.println("journal size: " + bus.journal().size()); // 3

        // --- 2. Undo последней команды ---
        System.out.println();
        System.out.println("-- 2. Undo последней команды (tx-003) --");
        bus.undoLast();
        // Откат: acc-D → acc-C, 250, key=tx-003:undo

        // --- 3. Явный retry упавшей команды ---
        System.out.println();
        System.out.println("-- 3. Retry упавшей команды --");

        // Симулируем нестабильный сервис: первые 2 вызова бросают исключение
        int[] callCount = {0};
        AccountService flaky = (from, to, amount, key) -> {
            if (++callCount[0] < 3) throw new RuntimeException("transient network error");
            System.out.printf("transfer %d: %s → %s [key=%s] (попытка %d)%n",
                    amount, from, to, key, callCount[0]);
        };

        var flakyCmd = new TransferCommand(flaky, "acc-A", "acc-D", 750, "tx-004");
        bus.submit(flakyCmd);  // автоматически выполнит retry до 3 раз
        System.out.println("journal size после retry: " + bus.journal().size()); // 4

        // --- 4. Журнал для аудита/replay ---
        System.out.println();
        System.out.println("-- 4. Журнал команд (аудит) --");
        System.out.println("Всего команд в журнале: " + bus.journal().size());
        // Любую команду из журнала можно повторить: bus.retry(bus.journal().get(i), 3)

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - команда — объект: хранит from/to/amount/key → undo и retry тривиальны");
        System.out.println("  - журнал: все операции зафиксированы до выполнения → аудит и replay");
        System.out.println("  - retry: flaky-сервис обрабатывается автоматически, без дублей");
        System.out.println("  - undo: обратная операция инкапсулирована в команде, не в вызывающем коде");
    }
}
