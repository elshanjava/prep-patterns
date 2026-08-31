package pattern.creational.abstractfactory.good;

import pattern.creational.abstractfactory.model.PspClient;
import pattern.creational.abstractfactory.model.StripeClient;
import pattern.creational.abstractfactory.model.StripeWebhookParser;
import pattern.creational.abstractfactory.model.WebhookParser;

final class StripeFactory implements PspFactory {
    @Override public PspClient     createClient() { return new StripeClient(); }
    @Override public WebhookParser createParser()  { return new StripeWebhookParser(); }
}
