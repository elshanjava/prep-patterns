package tdd.zpractice.transfer;

public class TransferService2 {

    public void transfer(Account2 from, Account2 to, long amount) {
        if (from == to) throw new IllegalArgumentException("same account");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");

        Account2 first = from.id().compareTo(to.id()) < 0 ? from : to;
        Account2 second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.balance() < amount) {
                    throw new InsufficientBalanceException(from.id(), from.balance(), amount);
                }
                from.debit(amount);
                to.credit(amount);
            }
        }

    }

}
