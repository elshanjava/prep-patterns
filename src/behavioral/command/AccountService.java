package behavioral.command;

interface AccountService {
    void transfer(String from, String to, long amount, String idempotencyKey);
}
