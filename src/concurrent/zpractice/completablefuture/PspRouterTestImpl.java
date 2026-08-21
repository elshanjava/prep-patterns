package concurrent.zpractice.completablefuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class PspRouterTestImpl {

  private final ExecutorService pool = Executors.newCachedThreadPool();
  private static final long TIMEOUT = 150;

  CompletableFuture<PspResponse> route (Payment p) {
    var stripe = callPsp("stripe", 30, false, p);
    var braintree = callPsp("braintree", 120, true, p);
    var adyen = callPsp("adyen", 110, true, p);

    return firstSuccessful(List.of(stripe, braintree, adyen));
  }

    CompletableFuture<List<PspResponse>> allQuotes (Payment p) {
        var stripe = callPsp("stripe", 100, true, p);
        var braintree = callPsp("braintree", 300, true, p);
        var adyen = callPsp("adyen", 90, true, p);

        return CompletableFuture.allOf(stripe, braintree, adyen)
                .thenApply(ignored -> Stream.of(stripe, braintree, adyen)
                        .map(CompletableFuture::join)
                        .filter(PspResponse::success)
                        .toList());
    }

  private CompletableFuture<PspResponse> firstSuccessful(List<CompletableFuture<PspResponse>> calls) {
    var result = new CompletableFuture<PspResponse>();

    if (calls.isEmpty()) {
      result.completeExceptionally(new IllegalArgumentException("no PSPs to call"));
      return result;
    }

    var remaining = new AtomicInteger(calls.size());

    for (var call: calls) {
      // whenComplete, а не thenAccept + allOf().thenRun(): обе те ветки ПРОПУСКАЮТСЯ
      // при исключительном завершении. Передай сюда фьючу, упавшую без sentinel'а, —
      // result не завершится никогда, и вызывающий повиснет на join() навсегда.
      call.whenComplete((r, ex) -> {
        if (ex == null && r != null && r.success()) {
          result.complete(r);                      // complete идемпотентен: победитель один
        }
        if (remaining.decrementAndGet() == 0) {
          result.completeExceptionally(new RuntimeException("all PSPs failed"));
        }
      });
    }

    return result;
  }

  private CompletableFuture<PspResponse> callPsp(String name, long latency, boolean healthy, Payment p) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        Thread.sleep(latency);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      if (!healthy) {
        throw new RuntimeException(name + " unavailable");
      }
      System.out.println('[' + name + ']' + " respond from " + p.id());
      return new PspResponse(name, latency, true);
    }, pool)
        .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS)   // МИЛЛИ-, не МИКРО-: 150 мкс = 0.15 мс,
                                                     // по такому бюджету отваливались ВСЕ PSP
        .exceptionally(ex -> {
          System.out.println("  [" + name + "] FAILED: " + rootMessage(ex));
          return new PspResponse(name, -1, false);
        });
  }

  private String rootMessage(Throwable ex) {
    Throwable c = ex;
    while (c.getCause() != null) c = c.getCause();
    return c.getClass().getSimpleName() + (c.getMessage() == null ? "" : ": " + c.getMessage());
  }

}
