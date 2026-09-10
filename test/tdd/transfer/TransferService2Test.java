package tdd.transfer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.zpractice.transfer.Account2;
import tdd.zpractice.transfer.InsufficientBalanceException;
import tdd.zpractice.transfer.TransferService2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TransferService2Test {
    private TransferService2 transferService;
    private Account2 from;
    private Account2 to;

    @BeforeEach
    void setUp() {
        transferService = new TransferService2();
        from = new Account2("account-1", 1000_00);
        to = new Account2("account-2", 800_00);
    }

    @Test
    void transfer_debitFromAndCreditTo() {
        transferService.transfer(from, to, 100_00);

        assertThat(from.balance()).isEqualTo(900_00);
        assertThat(to.balance()).isEqualTo(900_00);
    }

    @Test
    void transfer_throwsWhenFundsNotEnough() {
        assertThatThrownBy(()-> transferService.transfer(from, to, 2000_00))
                .isInstanceOf(InsufficientBalanceException.class);

        assertThat(from.balance()).isEqualTo(1000_00);
        assertThat(to.balance()).isEqualTo(800_00);
    }

    @Test
    void transfer_throwsOnSameAccount() {
        assertThatThrownBy(()-> transferService.transfer(from, from, 100_00))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void transfer_throwsOnNonPositiveAmount() {
        assertThatThrownBy(()-> transferService.transfer(from, to, 0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(()-> transferService.transfer(from, to, -100_00))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void transfer_concurrent_nonOverdraft() throws InterruptedException {
        int threads = 2000;
        var ready = new CountDownLatch(threads);
        var done = new CountDownLatch(threads);
        long amount = 1_00;

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(()-> {
                    ready.countDown();
                    try {
                        ready.await();
                        for (int j = 0; j < 50; j++) {
                            transferService.transfer(from, to, amount);
                        }

                    } catch (InsufficientBalanceException | IllegalArgumentException e) {

                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertThat(done.await(100, TimeUnit.MILLISECONDS)).isTrue();
        }

        assertThat(from.balance()).isEqualTo(0);
        assertThat(to.balance()).isEqualTo(1800_00);
    }

    @Test
    void transfer_concurrent_nonDeadLock() throws InterruptedException {
        var ready = new CountDownLatch(2);
        var done = new CountDownLatch(2);

        Thread transfer1 = new Thread(()-> {
            ready.countDown();
            try {
                ready.await();
                for (int i = 0; i < 100; i++) {
                    try {
                        transferService.transfer(from, to, 100_00);
                    } catch (InsufficientBalanceException ibe){}
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }

        });

        Thread transfer2 = new Thread(()-> {
            ready.countDown();
            try {
                ready.await();
                for (int i = 0; i < 100; i++) {
                    try {
                        transferService.transfer(to, from, 100_00);
                    } catch (InsufficientBalanceException ibe){}
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                done.countDown();
            }

        });

        transfer1.start();
        transfer2.start();

        assertThat(done.await(100, TimeUnit.MILLISECONDS)).isTrue();
    }
}
