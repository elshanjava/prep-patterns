package concurrent.completablefuture.good;

import concurrent.completablefuture.model.Payment;

public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== CompletableFuture [GOOD] — параллельные вызовы PSP ==");

        var router = new PspRouter();

        // anyOf: первый ответивший PSP
        System.out.println("--- anyOf: первый ответивший ---");
        long start = System.currentTimeMillis();
        var resp = router.route(new Payment("pay-1", 5000L)).get();
        System.out.printf("winner: %s  время: %d ms (вместо ~300ms последовательно)%n",
                resp.psp(), System.currentTimeMillis() - start);

        System.out.println();

        // allOf: все котировки параллельно
        System.out.println("--- allOf: все котировки ---");
        long start2 = System.currentTimeMillis();
        var quotes = router.allQuotes(new Payment("pay-2", 5000L)).get();
        System.out.printf("время allQuotes: %d ms (вместо ~300ms последовательно)%n",
                System.currentTimeMillis() - start2);
        quotes.stream()
              .sorted(java.util.Comparator.comparingLong(r -> r.feeMillis()))
              .forEach(r -> System.out.println("  " + r.psp() + " fee=" + r.feeMillis() + "ms"));

        router.shutdown();

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - anyOf: latency = max(PSP) вместо sum(PSP)");
        System.out.println("  - allOf: все котировки за 1 round-trip, выбираем дешевейший");
        System.out.println("  - virtual threads: миллион параллельных вызовов без OOM");
        System.out.println("  - добавить 4-й PSP: одна строка callPsp() + добавить в anyOf/allOf");
        System.out.println("  - в Spring WebFlux: Mono.zip() / Flux.merge() — тот же паттерн реактивно");
    }
}
