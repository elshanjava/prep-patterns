package tdd.loadbalancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.loadbalancer.LoadBalancer2;
import tdd.zpractice.loadbalancer.Strategy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LoadBalancer2Test {

    private LoadBalancer2 loadBalancer2;

    @BeforeEach
    void setUp() {
        loadBalancer2 = new LoadBalancer2();
    }

    @Test
    void registerAndGetSuccess() {
        loadBalancer2.register("10.0.0.1");
        assertThat(loadBalancer2.get()).isEqualTo("10.0.0.1");
    }

    @Test
    void shouldSaveSizeOfServers() {
        loadBalancer2.register("10.0.0.1");
        loadBalancer2.register("10.0.0.2");
        loadBalancer2.register("10.0.0.3");

        assertThat(loadBalancer2.size()).isEqualTo(3);
    }

    @Test
    void serversShouldBeRandomStrategy() {
        loadBalancer2.register("10.0.0.1");
        loadBalancer2.register("10.0.0.2");
        loadBalancer2.register("10.0.0.3");

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
             seen.add(loadBalancer2.get());
        }

        assertThat(seen).containsExactlyInAnyOrder("10.0.0.1", "10.0.0.2", "10.0.0.3");
    }

    @Test
    void get_shouldBeRoundRobinStrategy() {
        LoadBalancer2 balancerRR = new LoadBalancer2(Strategy.ROUND_ROBIN);

        balancerRR.register("10.0.0.1");
        balancerRR.register("10.0.0.2");
        balancerRR.register("10.0.0.3");

        assertThat(balancerRR.get()).isEqualTo("10.0.0.1");
        assertThat(balancerRR.get()).isEqualTo("10.0.0.2");
        assertThat(balancerRR.get()).isEqualTo("10.0.0.3");
        assertThat(balancerRR.get()).isEqualTo("10.0.0.1");
    }

    @Test
    void deregister_decreasesSize() {
        loadBalancer2.register("10.0.0.1");
        loadBalancer2.register("10.0.0.2");
        loadBalancer2.register("10.0.0.3");

        loadBalancer2.deregister("10.0.0.2");

        assertThat(loadBalancer2.size()).isEqualTo(2);

    }

    @Test
    void deregister_throwsWhenServerNotFound() {
        assertThatThrownBy(()-> loadBalancer2.deregister("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_throwsWhenNoServersRegistered() {
        assertThatThrownBy(()-> loadBalancer2.get())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void register_throwsWhenServerDuplicate() {
        loadBalancer2.register("10.0.0.1");

        assertThatThrownBy(()-> loadBalancer2.register("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_throwNullPointerWhenNull() {
        assertThatThrownBy(()-> loadBalancer2.register(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void register_hasMaxCapacity() {
        for (int i = 0; i < 10; i++) {
             loadBalancer2.register("10.0.0." + i);
        }

        assertThatThrownBy(()-> loadBalancer2.register("last"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getAndDeregister_threadSafe() throws InterruptedException {
        for (int i = 0; i < 10; i++) loadBalancer2.register("10.0.0." + i);

        int getters = 20;
        int deregisters = 8;
        int total = getters + deregisters;

        var ready = new CountDownLatch(total);
        var done = new CountDownLatch(total);
        var errorsCounter = new AtomicInteger(0);

        try (ExecutorService pool = Executors.newFixedThreadPool(total)) {
            for (int i = 0; i < getters; i++) {
                 pool.submit(()->{
                    ready.countDown();
                    try {
                        ready.await();
                        for (int k = 0; k < 1000; k++) {    // крутим get МНОГО раз
                            try { loadBalancer2.get(); }
                            catch (IllegalStateException ignored) {}   // пустой пул — норма
                            catch (Exception e) { errorsCounter.incrementAndGet(); }     // IndexOOB — БАГ
                        }
                    }  catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally { done.countDown(); }
                 });
            }

            for (int i = 0; i < deregisters; i++) {
                 final String server = "10.0.0." + i;

                 pool.submit(()-> {
                     ready.countDown();
                     try {
                         ready.await();
                         for (int k = 0; k < 1000; k++) {     // ← churn весь тест
                             try { loadBalancer2.deregister(server); } catch (IllegalArgumentException ignored) {}
                             try { loadBalancer2.register(server);   } catch (IllegalArgumentException | IllegalStateException | NullPointerException ignored) {}
                         }
                     } catch (IllegalArgumentException iae) {
//                         expected
                     } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                     } catch (Exception e) {
                         errorsCounter.incrementAndGet();
                     } finally {
                         done.countDown();
                     }
                 });
            }
            done.await();
        }

        assertThat(errorsCounter.get()).isZero();
    }






}
