package concurrent.completablefuture.good;

import concurrent.completablefuture.model.Payment;
import concurrent.completablefuture.model.PspResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

final class PspRouter {
    // Java 17: виртуальных потоков ещё нет (они с Java 21), поэтому обычный кэширующий пул.
    // Для I/O-bound вызовов PSP (потоки спят на сети) cached pool достаточно.
    private final ExecutorService pool = Executors.newCachedThreadPool();

    // Бюджет на один PSP: превысил — считаем его отвалившимся, дальше не ждём.
    private static final long TIMEOUT_MS = 150;

    // "первый УСПЕШНЫЙ": stripe тут падает быстро, но выиграть гонку он не должен.
    CompletableFuture<PspResponse> route(Payment p) {
        var stripe    = callPsp("stripe",     30, false, p);  // упадёт на 30ms
        var braintree = callPsp("braintree", 120, true,  p);
        var adyen     = callPsp("adyen",      110, true,  p);  // самый быстрый из живых → winner

        return firstSuccessful(List.of(stripe, braintree, adyen));
    }

    // allOf: собираем котировки, но braintree превышает таймаут — его отбрасываем.
    CompletableFuture<List<PspResponse>> allQuotes(Payment p) {
        var stripe    = callPsp("stripe",    100, true, p);
        var braintree = callPsp("braintree", 300, true, p);  // > TIMEOUT_MS → отвалится по таймауту
        var adyen     = callPsp("adyen",      90, true, p);

        return CompletableFuture.allOf(stripe, braintree, adyen)
                .thenApply(ignored -> Stream.of(stripe, braintree, adyen)
                        .map(CompletableFuture::join)      // все уже готовы → join не блокирует
                        .filter(PspResponse::success)      // выбрасываем упавшие/таймаут
                        .toList());
    }

    // anyOf берёт первого ЗАВЕРШИВШЕГОСЯ (в т.ч. с ошибкой), а нам нужен первый УСПЕШНЫЙ.
    // Собираем вручную: каждый успех пытается "занять" result; complete() идемпотентен —
    // первый победитель фиксируется, остальные вызовы игнорируются.
    private CompletableFuture<PspResponse> firstSuccessful(List<CompletableFuture<PspResponse>> calls) {
        var result = new CompletableFuture<PspResponse>();

        for (var call : calls) {
            call.thenAccept(r -> { if (r.success()) result.complete(r); });
        }

        // когда завершились ВСЕ, а result так и не заняли — значит никто не преуспел
        CompletableFuture.allOf(calls.toArray(CompletableFuture[]::new))
                .thenRun(() -> result.completeExceptionally(
                        new RuntimeException("all PSPs failed")));

        return result;
    }

    private CompletableFuture<PspResponse> callPsp(String name, long latencyMs, boolean healthy, Payment p) {
        return CompletableFuture.supplyAsync(() -> {
                    try { Thread.sleep(latencyMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    if (!healthy) throw new RuntimeException(name + " unavailable");
                    System.out.println("  [" + name + "] responded for " + p.id());
                    return new PspResponse(name, latencyMs, true);
                }, pool)
                // per-PSP таймаут: задачу в пуле не отменяет, но фьючу завершает ошибкой (Java 9+)
                .orTimeout(TIMEOUT_MS, TimeUnit.MILLISECONDS)
                // любой сбой (падение или таймаут) → sentinel success=false; фьюча всегда завершается штатно
                .exceptionally(ex -> {
                    System.out.println("  [" + name + "] FAILED: " + rootMessage(ex));
                    return new PspResponse(name, -1, false);
                });
    }

    private static String rootMessage(Throwable ex) {
        Throwable c = ex;
        while (c.getCause() != null) c = c.getCause();
        return c.getClass().getSimpleName() + (c.getMessage() == null ? "" : ": " + c.getMessage());
    }

    void shutdown() { pool.shutdown(); }
}
