package pattern.creational.abstractfactory.good;

import pattern.creational.abstractfactory.model.AdyenClient;
import pattern.creational.abstractfactory.model.AdyenWebhookParser;
import pattern.creational.abstractfactory.model.PspClient;
import pattern.creational.abstractfactory.model.WebhookParser;

final class AdyenFactory implements PspFactory {
    @Override public PspClient     createClient() { return new AdyenClient(); }
    @Override public WebhookParser createParser()  { return new AdyenWebhookParser(); }
}
