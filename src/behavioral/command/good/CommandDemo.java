package behavioral.command.good;

import behavioral.command.model.AccountService;

public class CommandDemo {
    public static void main(String[] args) {
        System.out.println("== Command [GOOD] ==");

        AccountService accounts = (from, to, amount, key) ->
                System.out.printf("transfer %d: %s → %s [%s]%n", amount, from, to, key);

        var cmd = new TransferCommand(accounts, "acc-A", "acc-B", 500, "tx-001");
        var bus = new CommandBus();
        bus.submit(cmd);
        System.out.println("journal size: " + bus.journal().size());

        System.out.println("-- undo --");
        cmd.undo();
    }
}
