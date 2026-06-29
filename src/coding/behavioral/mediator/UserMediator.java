package coding.behavioral.mediator;

public interface UserMediator {
    void register(User user);
    void send(String message, User sender);
}
