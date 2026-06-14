package tdd.idempotency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Idempotent Processor — TDD kata (Revolut Coding Exercise Q136).
 *
 * "Ensure processPayment(request) processes each unique requestId
 *  exactly once, even under concurrent calls."
 *
 *   1. первый вызов выполняет action и возвращает результат
 *   2. тот же requestId → тот же результат, action не вызывается повторно
 *   3. разные requestId → оба выполняются
 *   4. упавший action → не кэшируется (можно повторить)
 *   5. concurrent — ровно одно выполнение при одновременных вызовах
 */
class IdempotentProcessorTest {

    private IdempotentProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new IdempotentProcessor();
    }

    // ── Шаг 1: первый вызов выполняет action ─────────────────────────────────

    @Test
    void process_firstCallExecutesAction() throws Exception {
        String result = processor.process("req-1", () -> "payment processed");

        assertThat(result).isEqualTo("payment processed");
    }

    // ── Шаг 2: повторный requestId — возвращает кэш, action не выполняется ──

    @Test
    void process_sameRequestId_returnsCachedResult() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);

        processor.process("req-1", () -> { callCount.incrementAndGet(); return "ok"; });
        processor.process("req-1", () -> { callCount.incrementAndGet(); return "ok"; });
        processor.process("req-1", () -> { callCount.incrementAndGet(); return "ok"; });

        assertThat(callCount.get()).isEqualTo(1);
    }

    // ── Шаг 3: разные requestId → оба выполняются ────────────────────────────

    @Test
    void process_differentRequestIds_bothExecute() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);

        processor.process("req-1", () -> { callCount.incrementAndGet(); return "r1"; });
        processor.process("req-2", () -> { callCount.incrementAndGet(); return "r2"; });

        assertThat(callCount.get()).isEqualTo(2);
    }

    @Test
    void process_differentRequestIds_returnCorrectResults() throws Exception {
        String r1 = processor.process("req-1", () -> "result-1");
        String r2 = processor.process("req-2", () -> "result-2");

        assertThat(r1).isEqualTo("result-1");
        assertThat(r2).isEqualTo("result-2");
    }

    // ── Шаг 4: упавший action → не кэшируется, можно повторить ──────────────

    @Test
    void process_failedAction_isNotCached() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);

        // первый вызов падает
        assertThatThrownBy(() -> processor.process("req-1", () -> {
            callCount.incrementAndGet();
            throw new RuntimeException("PSP timeout");
        })).isInstanceOf(RuntimeException.class);

        // второй вызов должен выполниться (не закэшировать ошибку)
        processor.process("req-1", () -> { callCount.incrementAndGet(); return "ok"; });

        assertThat(callCount.get()).isEqualTo(2);
    }

    // ── Шаг 5: concurrent — ровно одно выполнение ────────────────────────────

    @Test
    void process_concurrent_executedExactlyOnce() throws InterruptedException {
        int threads = 50;
        AtomicInteger callCount = new AtomicInteger(0);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        processor.process("req-1", () -> {
                            callCount.incrementAndGet();
                            Thread.sleep(5); // имитация обращения к БД/PSP
                            return "ok";
                        });
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        assertThat(callCount.get()).isEqualTo(1);
    }
}
