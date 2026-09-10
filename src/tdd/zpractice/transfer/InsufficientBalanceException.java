package tdd.zpractice.transfer;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String accountId, long balance, long amount) {
        super("User with account id: " + accountId + " want to transfer: " + amount + " but balance is: " + balance);
    }
}
