package tdd.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

/**
 * Thread-safe Transfer — TDD kata (Revolut Live Coding / Tech Interview).
 *
 * Конкретно упоминается в отзывах: "дали скелет Account + TransferService,
 * попросили реализовать transfer() с ReentrantLock, потом вопрос про deadlock".
 *
 *   1. базовый перевод — баланс изменился
 *   2. недостаточно средств → исключение
 *   3. отрицательная сумма → исключение
 *   4. перевод самому себе → исключение
 *   5. конкурентные переводы — нет overdraft
 *   6. нет deadlock: A→B и B→A одновременно
 *   7. сохранение суммарного баланса (money conservation)
 */
class TransferServiceTest {

    private TransferService service;
    private Account alice;
    private Account bob;

    @BeforeEach
    void setUp() {
        service = new TransferService();
        alice   = new Account("alice", 1000_00L); // 1000.00
        bob     = new Account("bob",   500_00L);  // 500.00
    }

    // ── Шаг 1: базовый перевод ────────────────────────────────────────────────

    @Test
    void transfer_debitsFromAndCreditTo() {
        service.transfer(alice, bob, 200_00L);

        assertThat(alice.balance()).isEqualTo(800_00L);
        assertThat(bob.balance()).isEqualTo(700_00L);
    }

    // ── Шаг 2: недостаточно средств ──────────────────────────────────────────

    @Test
    void transfer_throwsWhenInsufficientFunds() {
        assertThatThrownBy(() -> service.transfer(alice, bob, 2000_00L))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("alice");

        // баланс не изменился
        assertThat(alice.balance()).isEqualTo(1000_00L);
        assertThat(bob.balance()).isEqualTo(500_00L);
    }

    // ── Шаг 3: отрицательная сумма ───────────────────────────────────────────

    @Test
    void transfer_throwsOnNegativeAmount() {
        assertThatThrownBy(() -> service.transfer(alice, bob, -100L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void transfer_throwsOnZeroAmount() {
        assertThatThrownBy(() -> service.transfer(alice, bob, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Шаг 4: перевод самому себе ───────────────────────────────────────────

    @Test
    void transfer_throwsWhenSameAccount() {
        assertThatThrownBy(() -> service.transfer(alice, alice, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same account");
    }

    // ── Шаг 5: concurrent — нет overdraft ────────────────────────────────────

    @Test
    void transfer_concurrent_noOverdraft() throws InterruptedException {
        // alice: 1000.00, 20 потоков пытаются снять по 100.00 — только 10 должны успеть
        int threads = 20;
        long amount = 100_00L;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch done  = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        ready.await();
                        service.transfer(alice, bob, amount);
                    } catch (InsufficientFundsException | InterruptedException ignored) {
                    } finally {
                        done.countDown();
                    }
                });
            }
            done.await();
        }

        assertThat(alice.balance()).isGreaterThanOrEqualTo(0);
        assertThat(alice.balance() % amount).isEqualTo(0); // списания кратны 100.00
    }

    // ── Шаг 6: нет deadlock — A→B и B→A одновременно ────────────────────────

    @Test
    void transfer_concurrent_noDeadlock() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch done  = new CountDownLatch(2);

        // поток 1: alice → bob
        Thread t1 = new Thread(() -> {
            ready.countDown();
            try {
                ready.await();
                for (int i = 0; i < 100; i++) {
                    try { service.transfer(alice, bob, 1_00L); }
                    catch (InsufficientFundsException ignored) {}
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { done.countDown(); }
        });

        // поток 2: bob → alice (обратное направление)
        Thread t2 = new Thread(() -> {
            ready.countDown();
            try {
                ready.await();
                for (int i = 0; i < 100; i++) {
                    try { service.transfer(bob, alice, 1_00L); }
                    catch (InsufficientFundsException ignored) {}
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { done.countDown(); }
        });

        t1.start();
        t2.start();
        // если deadlock — тест зависнет и упадёт по таймауту
        assertThat(done.await(5, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
    }

    // ── Шаг 7: сумма денег сохраняется ───────────────────────────────────────

    @Test
    void transfer_conservesMoney() throws InterruptedException {
        long totalBefore = alice.balance() + bob.balance();
        int threads = 50;
        CountDownLatch done = new CountDownLatch(threads);

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                boolean aliceToBob = i % 2 == 0;
                pool.submit(() -> {
                    try {
                        if (aliceToBob) service.transfer(alice, bob, 10_00L);
                        else            service.transfer(bob, alice, 10_00L);
                    } catch (InsufficientFundsException ignored) {
                    } finally { done.countDown(); }
                });
            }
            done.await();
        }

        long totalAfter = alice.balance() + bob.balance();
        assertThat(totalAfter).isEqualTo(totalBefore);
    }
}
