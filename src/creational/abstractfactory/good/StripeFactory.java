package creational.abstractfactory.good;

import creational.abstractfactory.model.PspClient;
import creational.abstractfactory.model.StripeClient;
import creational.abstractfactory.model.StripeWebhookParser;
import creational.abstractfactory.model.WebhookParser;

final class StripeFactory implements PspFactory {
    @Override public PspClient     createClient() { return new StripeClient(); }
    @Override public WebhookParser createParser()  { return new StripeWebhookParser(); }
}
