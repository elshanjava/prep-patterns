package tdd.lrucache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

/**
 * LRU Cache — TDD kata (классика финтех-лайвкодинга, идёт сразу за rate limiter'ом).
 *
 * "Сделай кэш на N записей: при переполнении выкидывается тот, к которому дольше всего
 *  не обращались. Потом — а теперь потокобезопасно."
 *
 *   1. положили — прочитали
 *   2. отсутствующий ключ — null
 *   3. переполнение — вытесняется самый давний
 *   4. get ОБНОВЛЯЕТ свежесть (ключевое отличие LRU от FIFO)
 *   5. повторный put не растит размер и тоже обновляет свежесть
 *   6. неположительная ёмкость — исключение
 *   7. containsKey не влияет на порядок
 *   8. thread-safe — ёмкость не превышается под нагрузкой
 */
class LruCacheTest {

    private LruCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new LruCache<>(3);
    }

    // ── Шаг 1: положили — прочитали ──────────────────────────────────────────

    @Test
    void get_returnsStoredValue() {
        cache.put("a", 1);

        assertThat(cache.get("a")).isEqualTo(1);
    }

    // ── Шаг 2: отсутствующий ключ — null ─────────────────────────────────────

    @Test
    void get_returnsNullForMissingKey() {
        assertThat(cache.get("nope")).isNull();
    }

    // ── Шаг 3: переполнение — вытесняется самый давний ───────────────────────

    @Test
    void put_evictsEldestWhenOverCapacity() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 4);          // ёмкость 3 — "a" должен уйти

        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.get("a")).isNull();
        assertThat(cache.get("b")).isEqualTo(2);
        assertThat(cache.get("c")).isEqualTo(3);
        assertThat(cache.get("d")).isEqualTo(4);
    }

    // ── Шаг 4: get обновляет свежесть — LRU, а не FIFO ───────────────────────

    @Test
    void get_refreshesRecency_soEldestIsNotTheFirstInserted() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        cache.get("a");             // "a" снова самый свежий

        cache.put("d", 4);          // теперь вытесняться должен "b", а не "a"

        assertThat(cache.get("a")).isEqualTo(1);
        assertThat(cache.get("b")).isNull();
    }

    // ── Шаг 5: повторный put обновляет значение, размер и свежесть ───────────

    @Test
    void put_existingKey_updatesValueWithoutGrowing() {
        cache.put("a", 1);
        cache.put("a", 99);

        assertThat(cache.size()).isEqualTo(1);
        assertThat(cache.get("a")).isEqualTo(99);
    }

    @Test
    void put_existingKey_refreshesRecency() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        cache.put("a", 11);         // перезапись тоже делает "a" свежим
        cache.put("d", 4);

        assertThat(cache.get("a")).isEqualTo(11);
        assertThat(cache.get("b")).isNull();
    }

    // ── Шаг 6: неположительная ёмкость — исключение ──────────────────────────

    @Test
    void constructor_rejectsNonPositiveCapacity() {
        assertThatThrownBy(() -> new LruCache<String, Integer>(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacity");

        assertThatThrownBy(() -> new LruCache<String, Integer>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Шаг 7: containsKey не трогает порядок ────────────────────────────────

    @Test
    void containsKey_doesNotAffectEvictionOrder() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        cache.containsKey("a");     // в отличие от get — не освежает
        cache.put("d", 4);

        assertThat(cache.get("a")).isNull();          // всё равно вытеснен
        assertThat(cache.keysOldestFirst()).containsExactly("b", "c", "d");
    }

    // ── Шаг 8: thread-safe — ёмкость не превышается ──────────────────────────

    @Test
    void put_threadSafe_neverExceedsCapacity() throws InterruptedException {
        LruCache<Integer, Integer> shared = new LruCache<>(50);
        int threads   = 32;
        int perThread = 200;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                int base = t * perThread;
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        for (int i = 0; i < perThread; i++) {
                            shared.put(base + i, i);
                            shared.get(base + i);          // get тоже мутирует — тоже под нагрузкой
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        // Без синхронизации LinkedHashMap здесь либо перекосит размер, либо зациклит
        // обход при resize. Ровно поэтому все методы synchronized.
        assertThat(shared.size()).isEqualTo(50);
        assertThat(shared.keysOldestFirst()).hasSize(50);
    }
}
