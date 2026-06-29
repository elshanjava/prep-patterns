package coding.behavioral.mediator;

public class Main {

    public static void main(String[] args) {
        UserMediator mediator = new UserOrchestrator();

        // room 1
        User alice = new User("Alice", 1L, mediator);
        User bob = new User("Bob", 1L, mediator);
        User carol = new User("Carol", 1L, mediator);

        // room 2
        User dave = new User("Dave", 2L, mediator);
        User erin = new User("Erin", 2L, mediator);

        // коллеги знают только медиатор; медиатор знает топологию
        mediator.register(alice);
        mediator.register(bob);
        mediator.register(carol);
        mediator.register(dave);
        mediator.register(erin);

        System.out.println("-- Alice пишет в room 1 (получают Bob и Carol, но не Alice и не room 2) --");
        alice.send("привет, комната 1");

        System.out.println();
        System.out.println("-- Dave пишет в room 2 (получает только Erin) --");
        dave.send("привет, комната 2");
    }
}
