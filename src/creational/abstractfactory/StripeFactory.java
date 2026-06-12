package creational.abstractfactory;

import creational.abstractfactory.model.StripeClient;
import creational.abstractfactory.model.StripeWebhookParser;

final class StripeFactory implements PspFactory {
    public PspClient createClient()            { return new StripeClient(); }
    public WebhookParser createWebhookParser() { return new StripeWebhookParser(); }
}
