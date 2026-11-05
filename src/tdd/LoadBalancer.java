package tdd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadBalancer {

//    1 Ability to register servers
//    2 Ability to deregister
//    3 Get servers
//    4 Provide different strategies

    private static final int MAX_SERVERS = 10;

    private final Strategy strategy;
    private final List<String> servers = new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger(0);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LoadBalancer() {
        this.strategy = Strategy.ROUND_ROBIN;
    }

    public LoadBalancer(Strategy strategy) {
        this.strategy = strategy;
    }

    public void register(String server) {
        lock.writeLock().lock();
        try {
            Objects.requireNonNull(server, "Server is null");
            if (servers.contains(server)) throw new IllegalArgumentException("Duplicate server");
            if (servers.size() >= MAX_SERVERS) throw new IllegalStateException("Max capacity reached");
            servers.add(server);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deregister(String server) {
        lock.writeLock().lock();
        try {
            if (!servers.remove(server)) {
                throw new IllegalArgumentException("Server " + server + " does not exist");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String get() {
        lock.readLock().lock();
        try {
            if (servers.isEmpty()) throw new IllegalStateException("Servers is empty");
            if (strategy == Strategy.RANDOM) {
                return random();
            }
            return roundRobin();
        } finally {
            lock.readLock().unlock();
        }
    }

    private String roundRobin() {
        return servers.get(Math.floorMod(index.getAndIncrement(), servers.size()));
    }

    private String random() {
        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }

    public int size() {
        lock.readLock().lock();
        try {
            return servers.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
