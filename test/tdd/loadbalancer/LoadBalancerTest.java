package tdd.loadbalancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

/**
 * Load Balancer — TDD kata (Revolut Live Coding).
 *
 * Требования добавляются постепенно — именно так на собесе:
 *   1. register + get одного сервера
 *   2. get случайный из нескольких
 *   3. нет дубликатов
 *   4. capacity limit (max 10)
 *   5. get из пустого балансера
 *   6. Round Robin
 *   7. deregister
 *   8. thread-safe register
 *   9. thread-safe get (Round Robin под нагрузкой)
 */
class LoadBalancerTest {

    private LoadBalancer balancer;

    @BeforeEach
    void setUp() {
        balancer = new LoadBalancer();
    }

    // ── Шаг 1: register одного сервера и получить его ────────────────────────

    @Test
    void get_returnsSingleRegisteredServer() {
        balancer.register("10.0.0.1");

        assertThat(balancer.get()).isEqualTo("10.0.0.1");
    }

    // ── Шаг 2: get возвращает один из зарегистрированных серверов ────────────

    @Test
    void get_returnsOneOfRegisteredServers() {
        balancer.register("10.0.0.1");
        balancer.register("10.0.0.2");
        balancer.register("10.0.0.3");

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            seen.add(balancer.get());
        }

        assertThat(seen).containsExactlyInAnyOrder("10.0.0.1", "10.0.0.2", "10.0.0.3");
    }

    // ── Шаг 3: нельзя зарегистрировать один сервер дважды ───────────────────

    @Test
    void register_throwsOnDuplicate() {
        balancer.register("10.0.0.1");

        assertThatThrownBy(() -> balancer.register("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    // ── Шаг 4: максимум 10 серверов ──────────────────────────────────────────

    @Test
    void register_throwsWhenCapacityExceeded() {
        for (int i = 1; i <= 10; i++) {
            balancer.register("10.0.0." + i);
        }

        assertThatThrownBy(() -> balancer.register("10.0.0.11"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("capacity");
    }

    // ── Шаг 5: get из пустого балансера бросает исключение ───────────────────

    @Test
    void get_throwsWhenNoServersRegistered() {
        assertThatThrownBy(() -> balancer.get())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no servers");
    }

    // ── Шаг 6: Round Robin — серверы возвращаются по кругу ───────────────────

    @Test
    void get_roundRobin_returnsServersInOrder() {
        LoadBalancer rr = new LoadBalancer(Strategy.ROUND_ROBIN);
        rr.register("10.0.0.1");
        rr.register("10.0.0.2");
        rr.register("10.0.0.3");

        assertThat(rr.get()).isEqualTo("10.0.0.1");
        assertThat(rr.get()).isEqualTo("10.0.0.2");
        assertThat(rr.get()).isEqualTo("10.0.0.3");
        assertThat(rr.get()).isEqualTo("10.0.0.1"); // wrap around
    }

    // ── Шаг 7: deregister убирает сервер ─────────────────────────────────────

    @Test
    void deregister_removesServer() {
        LoadBalancer rr = new LoadBalancer(Strategy.ROUND_ROBIN);
        rr.register("10.0.0.1");
        rr.register("10.0.0.2");

        rr.deregister("10.0.0.1");

        for (int i = 0; i < 10; i++) {
            assertThat(rr.get()).isEqualTo("10.0.0.2");
        }
    }

    @Test
    void deregister_throwsWhenServerNotFound() {
        assertThatThrownBy(() -> balancer.deregister("10.0.0.99"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not registered");
    }

    // ── Шаг 8: thread-safe register — нет дубликатов под конкурентной нагрузкой

    @Test
    void register_threadSafe_noDuplicatesUnderConcurrency() throws InterruptedException {
        int threads = 20;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);
        Set<Exception> errors = java.util.Collections.synchronizedSet(new HashSet<>());

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                final String server = "10.0.0." + (i % 10 + 1); // 10 уникальных + 10 дубликатов
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        balancer.register(server);
                    } catch (IllegalArgumentException | IllegalStateException ignored) {
                        // ожидаемо: дубликаты и capacity exceeded
                    } catch (Exception e) {
                        errors.add(e);
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        assertThat(errors).isEmpty(); // никаких неожиданных исключений
        assertThat(balancer.size()).isLessThanOrEqualTo(10); // capacity не нарушена
    }

    // ── Шаг 9: thread-safe get — Round Robin не теряет счётчик под нагрузкой ─

    @Test
    void get_roundRobin_threadSafe() throws InterruptedException {
        LoadBalancer rr = new LoadBalancer(Strategy.ROUND_ROBIN);
        rr.register("10.0.0.1");
        rr.register("10.0.0.2");
        rr.register("10.0.0.3");

        int threads = 30;
        CountDownLatch done = new CountDownLatch(threads);
        Set<String> results = java.util.Collections.synchronizedSet(new HashSet<>());

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    try { results.add(rr.get()); }
                    finally { done.countDown(); }
                });
            }
            done.await();
        }

        // все три сервера должны были получить запросы
        assertThat(results).containsExactlyInAnyOrder("10.0.0.1", "10.0.0.2", "10.0.0.3");
    }
}
