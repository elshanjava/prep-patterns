package concurrent.completablefuture.good;

import concurrent.completablefuture.model.Payment;
import concurrent.completablefuture.model.PspResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

final class PspRouter {
    // Вызовы к PSP — I/O-bound: поток спит на сети, а не считает. Проект на Java 21,
    // поэтому берём виртуальные потоки: их можно держать миллион, и размер пула
    // подбирать не нужно вовсе. На cached pool пришлось бы считать по формуле Литтла.
    private final ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

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
    //
    // Считаем отчитавшихся сами, а не через allOf().thenRun(). Причина: thenAccept и thenRun
    // при ИСКЛЮЧИТЕЛЬНОМ завершении пропускаются. Стоит передать сюда фьючу, завершившуюся
    // ошибкой (а не sentinel'ом из callPsp), — и обе ветки молчат, result не завершается
    // никогда, а вызывающий висит на join() вечно. whenComplete срабатывает на ОБОИХ исходах.
    private CompletableFuture<PspResponse> firstSuccessful(List<CompletableFuture<PspResponse>> calls) {
        var result = new CompletableFuture<PspResponse>();

        if (calls.isEmpty()) {
            result.completeExceptionally(new IllegalArgumentException("no PSPs to call"));
            return result;
        }

        var remaining = new AtomicInteger(calls.size());

        for (var call : calls) {
            call.whenComplete((r, ex) -> {
                if (ex == null && r != null && r.success()) {
                    result.complete(r);
                }
                // Последний отчитавшийся закрывает вопрос. Если победитель уже занял
                // result, completeExceptionally просто вернёт false и ничего не изменит.
                if (remaining.decrementAndGet() == 0) {
                    result.completeExceptionally(new RuntimeException("all PSPs failed"));
                }
            });
        }

        return result;
    }

    private CompletableFuture<PspResponse> callPsp(String name, long latencyMs, boolean healthy, Payment p) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(latencyMs);
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
