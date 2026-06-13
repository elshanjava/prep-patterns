package behavioral.command.model;

public interface AccountService {
    void transfer(String from, String to, long amount, String idempotencyKey);
}
