package tdd.zpractice.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer2 {

    private final Strategy strategy;
    private final List<String> servers = new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger(0);

    public LoadBalancer2() {
        this.strategy = Strategy.RANDOM;
    }

    public LoadBalancer2(Strategy strategy) {
        this.strategy = strategy;
    }

    public void register(String s) {
        if (servers.contains(s)) throw new IllegalArgumentException();
        this.servers.add(s);
    }


    public String get() {
        if (servers.isEmpty()) throw new IllegalStateException();
        if (strategy == Strategy.RANDOM) {
            return random();
        } else return roundRobin();
    }

    private String roundRobin() {
        return servers.get(Math.floorMod(index.getAndIncrement(), servers.size()));
    }

    public int size() {
        return servers.size();
    }

    private String random() {
        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }

    public void deregister(String s) {
        if (!servers.remove(s)) {
            throw new IllegalArgumentException("Server not found: " + s);
        }
    }
}
