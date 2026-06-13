package creational.abstractfactory.model;

public final class AdyenWebhookParser implements WebhookParser {
    public WebhookEvent parse(String body) {
        return new WebhookEvent("adyen.AUTHORISATION", body);
    }
}
