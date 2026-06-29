package coding.behavioral.mediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserOrchestrator implements UserMediator {

    private final Map<Long, List<User>> rooms = new HashMap<>();

    @Override
    public void register(User user) {
        rooms.computeIfAbsent(user.roomId(), k -> new ArrayList<>()).add(user);
    }

    @Override
    public void send(String message, User sender) {
        rooms.getOrDefault(sender.roomId(), List.of()).stream()
                .filter(u -> u != sender)
                .forEach(u -> {u.receive(message, sender);});
    }
}
