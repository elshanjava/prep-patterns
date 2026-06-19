package concurrent.completablefuture.good;

import concurrent.completablefuture.model.Payment;
import concurrent.completablefuture.model.PspResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class PspRouter {
    // Java 17: виртуальных потоков ещё нет (они с Java 21), поэтому обычный кэширующий пул.
    // Для I/O-bound вызовов PSP (потоки спят на сети) cached pool достаточно.
    private final ExecutorService pool = Executors.newCachedThreadPool();

    // anyOf: все 3 PSP параллельно — берём первый ответ (~100ms вместо ~300ms)
    CompletableFuture<PspResponse> route(Payment p) {
        var stripe    = callPsp("stripe",    100, p);
        var braintree = callPsp("braintree", 120, p);
        var adyen     = callPsp("adyen",     110, p);

        return CompletableFuture.anyOf(stripe, braintree, adyen)
                .thenApply(r -> (PspResponse) r);
    }

    // allOf: собираем все котировки параллельно (~100ms вместо ~300ms)
    CompletableFuture<List<PspResponse>> allQuotes(Payment p) {
        var stripe    = callPsp("stripe",    100, p);
        var braintree = callPsp("braintree",  80, p);
        var adyen     = callPsp("adyen",      90, p);

        return CompletableFuture.allOf(stripe, braintree, adyen)
                .thenApply(ignored -> List.of(stripe.join(), braintree.join(), adyen.join()));
    }

    private CompletableFuture<PspResponse> callPsp(String name, long latencyMs, Payment p) {
        return CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(latencyMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println("  [" + name + "] responded for " + p.id());
            return new PspResponse(name, latencyMs, true);
        }, pool);
    }

    void shutdown() { pool.shutdown(); }
}
