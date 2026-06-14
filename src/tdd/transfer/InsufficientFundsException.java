package tdd.transfer;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountId, long balance, long amount) {
        super("account " + accountId + " has " + balance + " but tried to debit " + amount);
    }
}
