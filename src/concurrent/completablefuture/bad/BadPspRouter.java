package concurrent.completablefuture.bad;

import concurrent.completablefuture.model.Payment;
import concurrent.completablefuture.model.PspResponse;

// Последовательный опрос PSP: ждём ответа каждого перед следующим вызовом.
// Если первый PSP медленный — клиент ждёт 100ms + 100ms + 100ms = 300ms.
// Нет возможности взять самый быстрый ответ — всегда идём по порядку.
final class BadPspRouter {
    PspResponse route(Payment p) throws InterruptedException {
        // Stripe — пробуем первым, ждём ответа
        Thread.sleep(100);
        System.out.println("  [stripe]    responded for " + p.id());
        return new PspResponse("stripe", 100, true);

        // Braintree и Adyen никогда не вызываются если Stripe ответил —
        // но если Stripe упадёт, мы потеряем 100ms перед следующей попыткой
    }

    // Для сбора всех котировок: 100 + 100 + 100 = 300ms суммарно
    java.util.List<PspResponse> allQuotes(Payment p) throws InterruptedException {
        Thread.sleep(100);
        var stripe = new PspResponse("stripe", 100, true);
        Thread.sleep(100);
        var braintree = new PspResponse("braintree", 80, true);
        Thread.sleep(100);
        var adyen = new PspResponse("adyen", 90, true);
        return java.util.List.of(stripe, braintree, adyen);
    }
}
