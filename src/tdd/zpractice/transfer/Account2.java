package tdd.zpractice.transfer;

public class Account2 {
    private final String accountId;
    private long balanceCents;

    public Account2(String accountId, long balanceCents) {
        this.accountId = accountId;
        this.balanceCents = balanceCents;
    }

    public String id() {
        return accountId;
    }

    public long balance() {
        return balanceCents;
    }

    void debit(long amount) {
        balanceCents -= amount;
    }

    void credit(long amount) {
        balanceCents += amount;
    }
}
