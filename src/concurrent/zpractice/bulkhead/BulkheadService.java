package concurrent.zpractice.bulkhead;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class BulkheadService {
    private final Semaphore permits;
    private final long wait;

    private final AtomicInteger inFlight      = new AtomicInteger();
    private final AtomicInteger maxObserved   = new AtomicInteger();
    private final AtomicInteger rejected      = new AtomicInteger();

    BulkheadService(int maxConcurrentCalls, long wait) {
        this.permits = new Semaphore(maxConcurrentCalls, true);
        this.wait = wait;
    }

    int availablePermits()        { return permits.availablePermits(); }
    int maxObservedConcurrency()  { return maxObserved.get(); }
    int rejectedCount()           { return rejected.get(); }

    <T> T call(Supplier<T> action) throws InterruptedException {
        if (!permits.tryAcquire(wait, TimeUnit.MILLISECONDS)) {
            rejected.incrementAndGet();
            throw new BulkheadFullException("bulkhead full: все " + permits.availablePermits() + " разрешений заняты");
        }
        try {
            int n = inFlight.incrementAndGet();
            maxObserved.accumulateAndGet(n, Math::max);
            return action.get();
        } finally {
            inFlight.decrementAndGet();
            permits.release();
        }
    }


}
