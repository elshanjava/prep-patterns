package behavioral.command;

final class TransferCommand implements Command {
    private final AccountService accounts;
    private final String from, to;
    private final long amount;
    private final String idempotencyKey;

    TransferCommand(AccountService accounts, String from, String to,
                    long amount, String idempotencyKey) {
        this.accounts = accounts;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    public void execute() {
        accounts.transfer(from, to, amount, idempotencyKey);
    }

    public void undo() {
        accounts.transfer(to, from, amount, idempotencyKey + ":undo");
    }
}
