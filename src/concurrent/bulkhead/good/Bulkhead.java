package concurrent.bulkhead.good;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

// Bulkhead ("переборка" — из судостроения: пробоина в одном отсеке не топит корабль).
// Ограничивает ЧИСЛО ОДНОВРЕМЕННЫХ вызовов к зависимости, чтобы одна медленная
// зависимость не съела все потоки сервиса и не утащила за собой всё остальное.
//
// Отличие от circuit breaker: тот реагирует на ОШИБКИ и рубит трафик целиком.
// Bulkhead ошибок не знает — он ограничивает ПАРАЛЛЕЛИЗМ и продолжает пропускать
// столько, сколько безопасно. На практике их ставят вместе.
//
// Отличие от rate limiter: тот ограничивает вызовы В ЕДИНИЦУ ВРЕМЕНИ (штук в секунду),
// bulkhead — вызовы ОДНОВРЕМЕННО (штук в полёте). При медленной зависимости важно второе.
final class Bulkhead {

    // Semaphore — счётчик разрешений, а НЕ замок:
    //   * нет владельца — освободить разрешение может другой поток;
    //   * нет реентерабельности — повторный acquire в том же потоке заберёт второе разрешение
    //     (и легко приведёт к самоблокировке);
    //   * разрешения можно добавлять и убирать на ходу.
    private final Semaphore permits;
    private final long      waitMs;

    private final AtomicInteger inFlight      = new AtomicInteger();
    private final AtomicInteger maxObserved   = new AtomicInteger();
    private final AtomicInteger rejected      = new AtomicInteger();

    Bulkhead(int maxConcurrentCalls, long waitMs) {
        // fair = true: без него поток может простоять произвольно долго, пока другие
        // проскакивают без очереди. Цена — пропускная способность.
        this.permits = new Semaphore(maxConcurrentCalls, true);
        this.waitMs  = waitMs;
    }

    <T> T call(Supplier<T> action) throws InterruptedException {
        // tryAcquire с таймаутом, а не acquire(): acquire() ждёт бесконечно, и тогда
        // перегрузка просто переезжает в очередь ожидающих потоков — те же грабли,
        // только незаметнее. Быстрый отказ лучше растущей очереди.
        if (!permits.tryAcquire(waitMs, TimeUnit.MILLISECONDS)) {
            rejected.incrementAndGet();
            throw new BulkheadFullException(
                    "bulkhead full: все " + permits.availablePermits() + " разрешений заняты");
        }
        try {
            int n = inFlight.incrementAndGet();
            maxObserved.accumulateAndGet(n, Math::max);
            return action.get();
        } finally {
            inFlight.decrementAndGet();
            // release ТОЛЬКО в finally. Пропустишь на исключении — разрешение утекло
            // навсегда, и после нескольких ошибок переборка закроется наглухо.
            permits.release();
        }
    }

    int availablePermits()        { return permits.availablePermits(); }
    int maxObservedConcurrency()  { return maxObserved.get(); }
    int rejectedCount()           { return rejected.get(); }
}
