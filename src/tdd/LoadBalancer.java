package tdd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalancer {

//    1 Ability to register servers
//    2 Ability to deregister
//    3 Get servers
//    4 Provide different strategies

    private static final int MAX_SERVERS = 10;

    private final Strategy strategy;
    private final List<String> servers = new ArrayList<>();
    private Integer index = 0;

    public LoadBalancer() {
        this.strategy = Strategy.ROUND_ROBIN;
    }

    public LoadBalancer(Strategy strategy) {
        this.strategy = strategy;
    }

    public void register(String server) {
        Objects.requireNonNull(server, "Server is null");
        if (servers.contains(server)) throw new IllegalArgumentException("Duplicate server");
        if (servers.size() >= MAX_SERVERS) throw new IllegalStateException("Max capacity reached");
        servers.add(server);

    }

    public void deregister(String server) {
        if (!servers.remove(server)) {
            throw new IllegalArgumentException("Server " + server + " does not exist");
        }
    }

    public String get() {
        if (servers.isEmpty()) throw new IllegalStateException("Servers is empty");
        if (strategy == Strategy.RANDOM) {
            return random();
        }
        return roundRobin();
    }

    private String roundRobin() {
        return servers.get(Math.floorMod(index++, servers.size()));
    }

    private String random() {
        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }

    public int size() {
        return servers.size();
    }
}
