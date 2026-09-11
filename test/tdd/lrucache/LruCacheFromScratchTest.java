package tdd.lrucache;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты к LruCacheFromScratch — в порядке TDD-шагов:
 * happy → вытеснение → get освежает (LRU, не FIFO) → put существующего → валидация → порядок.
 */
class LruCacheFromScratchTest {

    @Test
    void get_returnsStoredValue() {
        var cache = new LruCacheFromScratch<String, Integer>(2);
        cache.put("a", 1);
        assertThat(cache.get("a")).isEqualTo(1);
    }

    @Test
    void get_returnsNull_whenKeyAbsent() {
        var cache = new LruCacheFromScratch<String, Integer>(2);
        assertThat(cache.get("missing")).isNull();
    }

    @Test
    void put_evictsOldest_whenOverCapacity() {
        var cache = new LruCacheFromScratch<String, Integer>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);                       // переполнение → вытесняется "a" (самый старый)

        assertThat(cache.get("a")).isNull();
        assertThat(cache.get("b")).isEqualTo(2);
        assertThat(cache.get("c")).isEqualTo(3);
    }

    /** Ключевой тест LRU: обращение через get() освежает ключ, поэтому он НЕ вытесняется. */
    @Test
    void get_refreshesRecency_soLeastRecentlyUsedIsEvicted() {
        var cache = new LruCacheFromScratch<String, Integer>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");                          // "a" использован → теперь свежий; "b" стал самым старым
        cache.put("c", 3);                       // вытесняется "b", а НЕ "a" (иначе был бы FIFO)

        assertThat(cache.get("a")).isEqualTo(1); // выжил благодаря обращению
        assertThat(cache.get("b")).isNull();     // вытеснен
        assertThat(cache.get("c")).isEqualTo(3);
    }

    @Test
    void put_existingKey_updatesValueAndRefreshes_withoutGrowing() {
        var cache = new LruCacheFromScratch<String, Integer>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("a", 100);                     // обновляем "a" → и делаем свежим
        cache.put("c", 3);                       // вытесняется самый старый = "b"

        assertThat(cache.get("a")).isEqualTo(100); // значение обновилось И ключ выжил
        assertThat(cache.get("b")).isNull();        // вытеснен
        assertThat(cache.get("c")).isEqualTo(3);
        assertThat(cache.size()).isEqualTo(2);      // обновление не увеличило размер
    }

    @Test
    void constructor_throwsWhenCapacityNotPositive() {
        assertThatThrownBy(() -> new LruCacheFromScratch<String, Integer>(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LruCacheFromScratch<String, Integer>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void keysOldestFirst_reflectsAccessOrder() {
        var cache = new LruCacheFromScratch<String, Integer>(3);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);                       // порядок старый→свежий: a, b, c
        cache.get("a");                          // "a" освежён → b, c, a

        assertThat(cache.keysOldestFirst()).containsExactly("b", "c", "a");
    }

    /**
     * Под конкурентной нагрузкой структура не должна биться: никаких неожиданных
     * исключений (гонка на указателях списка дала бы NPE / зацикливание) и размер
     * никогда не превышает capacity. Ассёрты по инвариантам, не по конкретным значениям.
     */
    @Test
    void concurrent_holdsInvariants_underLoad() throws InterruptedException {
        var cache = new LruCacheFromScratch<Integer, Integer>(100);
        int threads = 50;
        var ready = new CountDownLatch(threads);
        var done  = new CountDownLatch(threads);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                final int base = t;
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        for (int i = 0; i < 1000; i++) {
                            int key = (base * 1000 + i) % 300;   // ключи пересекаются между потоками
                            cache.put(key, key);
                            cache.get(key);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        errors.add(e);                           // NPE/битый список — сюда
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(errors).isEmpty();                            // структура не побилась
        assertThat(cache.size()).isLessThanOrEqualTo(100);        // capacity не пробит
    }
}
