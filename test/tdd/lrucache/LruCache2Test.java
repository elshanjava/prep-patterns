package tdd.lrucache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.lrucache.LruCache2;

import static org.assertj.core.api.Assertions.assertThat;

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


}
