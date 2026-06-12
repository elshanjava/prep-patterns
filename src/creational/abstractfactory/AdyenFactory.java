package creational.abstractfactory;

import creational.abstractfactory.model.AdyenClient;
import creational.abstractfactory.model.AdyenWebhookParser;

final class AdyenFactory implements PspFactory {
    public PspClient createClient()            { return new AdyenClient(); }
    public WebhookParser createWebhookParser() { return new AdyenWebhookParser(); }
}
