package tdd.lrucache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.lrucache.LruCache2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LruCache2Test {

    private LruCache2<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new LruCache2<>(3);
    }

    @Test
    void get_returnsStoredValue() {
        cache.put("test", 1);
        assertThat(cache.get("test")).isEqualTo(1);
    }

    @Test
    void put_evictOldest_whenOverCapacity() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);

        assertThat(cache.get("a")).isNull();
        assertThat(cache.get("b")).isEqualTo(2);
        assertThat(cache.get("c")).isEqualTo(3);
        assertThat(cache.get("d")).isEqualTo(4);
    }

    @Test
    void get_movesAccessedKeyToFront() {
        LruCache2<String, Integer> cache2 = new LruCache2<>(2);
        cache2.put("a", 1);
        cache2.put("b", 2);

        cache2.get("a");
        cache2.put("c", 3);

        assertThat(cache2.get("a")).isNotNull();
        assertThat(cache2.get("a")).isEqualTo(1);
        assertThat(cache2.get("b")).isNull();
        assertThat(cache2.get("c")).isEqualTo(3);
    }

    @Test
    void put_refreshPosition_whenAlreadyExist() {
        LruCache2<String, Integer> cache2 = new LruCache2<>(2);
        cache2.put("a", 1);
        cache2.put("b", 2);
        cache2.put("a", 3);
        cache2.put("c", 4);

        assertThat(cache2.get("a")).isEqualTo(3);
        assertThat(cache2.get("c")).isEqualTo(4);

    }

    @Test
    void constructor_throwsWhenCapacityNotPositive() {
        assertThatThrownBy(()-> new LruCache2<>(0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(()-> new LruCache2<>(-5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void concurrent_holdsInvariants_underLoad() throws InterruptedException {
        LruCache2<Integer, Integer> cache2 = new LruCache2<>(100);
        int threads = 50;
        var ready = new CountDownLatch(threads);
        var done = new CountDownLatch(threads);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                final int base = i;
                pool.submit(()-> {
                    ready.countDown();
                    try {
                        ready.await();
                        for (int j = 0; j < 1000; j++) {
                            int key = (base * 1000 + j) % 300;   // ключи пересекаются между потоками
                            cache2.put(key, key);
                            cache2.get(key);
                        }
                    } catch (Exception e) {
                        errors.add(e);
                    } finally {
                        done.countDown();
                    }
                });

            }
            assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(errors.size()).isZero();


    }


}
