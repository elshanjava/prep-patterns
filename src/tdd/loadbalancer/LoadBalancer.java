package tdd.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadBalancer {

    private static final int MAX_CAPACITY = 10;

    private final Strategy              strategy;
    private final List<String>          servers = new ArrayList<>();
    private final ReentrantReadWriteLock lock   = new ReentrantReadWriteLock();
    private final AtomicInteger          index  = new AtomicInteger(0);
    private final Random                 random = new Random();

    public LoadBalancer() {
        this(Strategy.RANDOM);
    }

    public LoadBalancer(Strategy strategy) {
        this.strategy = strategy;
    }

    public void register(String server) {
        lock.writeLock().lock();
        try {
            if (servers.contains(server)) {
                throw new IllegalArgumentException(server + " already registered");
            }
            if (servers.size() >= MAX_CAPACITY) {
                throw new IllegalStateException("capacity limit reached (" + MAX_CAPACITY + ")");
            }
            servers.add(server);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deregister(String server) {
        lock.writeLock().lock();
        try {
            if (!servers.remove(server)) {
                throw new IllegalArgumentException(server + " not registered");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String get() {
        lock.readLock().lock();
        try {
            if (servers.isEmpty()) {
                throw new IllegalStateException("no servers registered");
            }
            return strategy == Strategy.ROUND_ROBIN ? roundRobin() : random();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try { return servers.size(); }
        finally { lock.readLock().unlock(); }
    }

    private String roundRobin() {
        int i = index.getAndIncrement() % servers.size();
        return servers.get(i);
    }

    private String random() {
        return servers.get(random.nextInt(servers.size()));
    }
}
