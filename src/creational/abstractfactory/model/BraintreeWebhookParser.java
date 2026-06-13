package creational.abstractfactory.model;

public final class BraintreeWebhookParser implements WebhookParser {
    public WebhookEvent parse(String body) {
        return new WebhookEvent("braintree.transaction.settled", body);
    }
}
