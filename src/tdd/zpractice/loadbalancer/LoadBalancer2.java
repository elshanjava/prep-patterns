package tdd.zpractice.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadBalancer2 {

    private static final int MAX_CAPACITY = 10;

    private final Strategy strategy;
    private final List<String> servers = new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger(0);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public LoadBalancer2() {
        this.strategy = Strategy.RANDOM;
    }

    public LoadBalancer2(Strategy strategy) {
        this.strategy = strategy;
    }

    public void register(String s) {
        lock.writeLock().lock();
        try {
            if (s == null) throw new NullPointerException("Server must not be null");
            if (servers.contains(s)) throw new IllegalArgumentException();
            if (size() >= MAX_CAPACITY) throw new IllegalStateException();
            servers.add(s);
        } finally {
            lock.writeLock().unlock();
        }
    }


    public String get() {
        lock.readLock().lock();
        try {
            if (servers.isEmpty()) throw new IllegalStateException();
            if (strategy == Strategy.RANDOM) {
                return random();
            } else return roundRobin();
        } finally {
            lock.readLock().unlock();
        }
    }

    private String roundRobin() {
        return servers.get(Math.floorMod(index.getAndIncrement(), servers.size()));
    }

    public int size() {
        lock.readLock().lock();
        try {
            return servers.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    private String random() {
        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }

    public void deregister(String s) {
        lock.writeLock().lock();
        try {
            if (!servers.remove(s)) {
                throw new IllegalArgumentException("Server not found: " + s);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
