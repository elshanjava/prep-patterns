package creational.abstractfactory.model;

public final class StripeWebhookParser implements WebhookParser {
    public WebhookEvent parse(String body) {
        return new WebhookEvent("stripe.payment_intent.succeeded", body);
    }
}
