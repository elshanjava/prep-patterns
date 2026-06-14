package concurrent.completablefuture.bad;

import concurrent.completablefuture.model.Payment;

public class BadCompletableFutureDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== CompletableFuture [BAD] — последовательные вызовы PSP ==");

        var router = new BadPspRouter();
        long start = System.currentTimeMillis();

        for (int i = 1; i <= 3; i++) {
            var p = new Payment("pay-" + i, i * 1000L);
            var resp = router.route(p);
            System.out.println("  routed " + p.id() + " via " + resp.psp());
        }

        System.out.printf("%nвремя (3 платежа, 1 PSP): %d ms%n", System.currentTimeMillis() - start);

        System.out.println();
        long start2 = System.currentTimeMillis();
        var quotes = router.allQuotes(new Payment("pay-quote", 5000L));
        System.out.printf("время allQuotes (3 PSP последовательно): %d ms%n", System.currentTimeMillis() - start2);
        quotes.forEach(r -> System.out.println("  " + r.psp() + " fee=" + r.feeMillis() + "ms"));

        System.out.println();
        System.out.println("Проблемы:");
        System.out.println("  - allQuotes: 3 × 100ms = 300ms вместо max(100,100,100) = 100ms");
        System.out.println("  - route: если Stripe упал — теряем 100ms перед попыткой Braintree");
        System.out.println("  - нет таймаута на отдельный PSP — один медленный блокирует всё");
    }
}
