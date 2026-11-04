package tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class LoadBalancerTest {

    private LoadBalancer loadBalancer;

    @BeforeEach
    void setUp() {
        loadBalancer = new LoadBalancer();
    }

    @Test
    void get_returnsSingleRegisteredServer() {
        loadBalancer.register("10.0.0.1");
        assertThat(loadBalancer.get()).isEqualTo("10.0.0.1");
    }

    @Test
    void get_roundRobin_cyclesThroughServers() {
        loadBalancer.register("10.0.0.1");
        loadBalancer.register("10.0.0.2");
        loadBalancer.register("10.0.0.3");

        assertThat(loadBalancer.get()).isEqualTo("10.0.0.1");
        assertThat(loadBalancer.get()).isEqualTo("10.0.0.2");
        assertThat(loadBalancer.get()).isEqualTo("10.0.0.3");
        assertThat(loadBalancer.get()).isEqualTo("10.0.0.1");
    }

    @Test
    void get_randomServer() {
        LoadBalancer randomLB = new LoadBalancer(Strategy.RANDOM);

        randomLB.register("10.0.0.1");
        randomLB.register("10.0.0.2");
        randomLB.register("10.0.0.3");

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            seen.add(randomLB.get());
        }

        assertThat(seen).containsExactlyInAnyOrder("10.0.0.1", "10.0.0.2", "10.0.0.3");
    }

    @Test
    void deregister_removesServer() {
        loadBalancer.register("10.0.0.1");
        loadBalancer.register("10.0.0.2");
        loadBalancer.register("10.0.0.3");

        loadBalancer.deregister("10.0.0.1");

        assertThat(loadBalancer.size()).isEqualTo(2);
    }

    @Test
    void deregister_throwsWhenServerDoNotFound() {
        assertThatThrownBy(()-> loadBalancer.deregister("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_throwsWhenServerIsEmpty() {
        assertThatThrownBy(()-> loadBalancer.get())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void register_throwsWhenServerIsNull() {
        assertThatThrownBy(()-> loadBalancer.register(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void register_throwsWhenDuplicate() {
        loadBalancer.register("10.0.0.1");
        assertThatThrownBy(()-> loadBalancer.register("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_throwsWhenLimitReached() {
        for (int i = 0; i < 10; i++) {
            loadBalancer.register("10.0.0" + i);
        }
        assertThatThrownBy(()-> loadBalancer.register("10.0.0.11"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getAndDeregister_threadSafe() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
             loadBalancer.register("10.0.0." + i);
        }

        int getters = 20;
        int deregisters = 8;
        int total = getters + deregisters;

        var ready = new CountDownLatch(total);
        var done = new CountDownLatch(total);
        var errorsCounter = new AtomicInteger(0);

        try (ExecutorService pool = Executors.newFixedThreadPool(total)) {
            for (int i = 0; i < getters; i++) {
                pool.submit(()-> {
                    ready.countDown();
                    try {
                        ready.await();
                        for (int j = 0; j < 1000; j++) {
                            loadBalancer.get();
                        }
                    }
                    catch (IndexOutOfBoundsException | NullPointerException e) {
                        errorsCounter.incrementAndGet();
                    }

                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }

            for (int i = 0; i < deregisters; i++) {
                final String server = "10.0.0." + i;
                 pool.submit(()-> {
                    ready.countDown();
                     try {
                         ready.await();
                         for (int j = 0; j < deregisters; j++) {
                              loadBalancer.deregister(server);
                              loadBalancer.register(server);
                         }
                     } catch (InterruptedException e) {
                         Thread.currentThread().interrupt();
                     } finally {
                         done.countDown();
                     }
                 });

            }
        }
        assertThat(done.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(errorsCounter.get()).isZero();

    }

}
