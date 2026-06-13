package creational.abstractfactory.good;

import creational.abstractfactory.model.AdyenClient;
import creational.abstractfactory.model.AdyenWebhookParser;
import creational.abstractfactory.model.PspClient;
import creational.abstractfactory.model.WebhookParser;

final class AdyenFactory implements PspFactory {
    @Override public PspClient     createClient() { return new AdyenClient(); }
    @Override public WebhookParser createParser()  { return new AdyenWebhookParser(); }
}
