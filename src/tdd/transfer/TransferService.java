package tdd.transfer;

public class TransferService {

    public void transfer(Account from, Account to, long amountCents) {
        if (from == to)      throw new IllegalArgumentException("same account");
        if (amountCents <= 0) throw new IllegalArgumentException("amount must be positive");

        // Упорядочиваем блокировки по id — предотвращает deadlock при A→B и B→A одновременно.
        // Без этого два потока могут взять первый замок и ждать друг друга вечно.
        Account first  = from.id().compareTo(to.id()) < 0 ? from : to;
        Account second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.balance() < amountCents) {
                    throw new InsufficientFundsException(from.id(), from.balance(), amountCents);
                }
                from.debit(amountCents);
                to.credit(amountCents);
            }
        }
    }
}
