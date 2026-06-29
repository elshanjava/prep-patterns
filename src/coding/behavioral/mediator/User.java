package coding.behavioral.mediator;

public class User {
    private final String name;
    private final Long roomId;
    private final UserMediator mediator;

    public User(String name, Long roomId, UserMediator mediator) {
        this.name = name;
        this.roomId = roomId;
        this.mediator = mediator;
    }

    public void send(String message) {
        mediator.send(message, this);
    }

    public void receive(String message, User from) {
        System.out.println("[room " + roomId + "] " + name + " <- " + from.name() + ": " + message);
    }

    public String name() {
        return name;
    }

    public Long roomId() {
        return roomId;
    }
}
