package creational.abstractfactory.good;

import creational.abstractfactory.model.BraintreeClient;
import creational.abstractfactory.model.BraintreeWebhookParser;
import creational.abstractfactory.model.PspClient;
import creational.abstractfactory.model.WebhookParser;

// Добавить Braintree: 1 файл + 2 строки в AbstractFactoryDemo.
// Компилятор гарантирует: BraintreeClient всегда с BraintreeWebhookParser.
final class BraintreeFactory implements PspFactory {
    @Override public PspClient     createClient() { return new BraintreeClient(); }
    @Override public WebhookParser createParser()  { return new BraintreeWebhookParser(); }
}
