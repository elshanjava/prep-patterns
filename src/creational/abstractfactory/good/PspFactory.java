package creational.abstractfactory.good;

import creational.abstractfactory.model.PspClient;
import creational.abstractfactory.model.WebhookParser;

// Абстрактная фабрика: гарантирует, что client и parser всегда от одного провайдера.
// Компилятор исключает мисмэтч типа «StripeClient + AdyenWebhookParser».
interface PspFactory {
    PspClient     createClient();
    WebhookParser createParser();
}
