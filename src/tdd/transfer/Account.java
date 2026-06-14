package tdd.transfer;

public class Account {
    private final String id;
    private long balanceCents;

    public Account(String id, long balanceCents) {
        this.id           = id;
        this.balanceCents = balanceCents;
    }

    public String id()      { return id; }
    public long balance()   { return balanceCents; }

    void debit(long amount)  { balanceCents -= amount; }
    void credit(long amount) { balanceCents += amount; }
}
