package pattern.creational.abstractfactory.good;

import pattern.creational.abstractfactory.model.PspClient;
import pattern.creational.abstractfactory.model.WebhookParser;

// Абстрактная фабрика: гарантирует, что client и parser всегда от одного провайдера.
// Компилятор исключает мисмэтч типа «StripeClient + AdyenWebhookParser».
interface PspFactory {
    PspClient     createClient();
    WebhookParser createParser();
}
