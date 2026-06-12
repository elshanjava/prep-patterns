package creational.abstractfactory.model;

import creational.abstractfactory.WebhookEvent;
import creational.abstractfactory.WebhookParser;

// Конкретный продукт «парсер» из семейства Stripe.
public class StripeWebhookParser implements WebhookParser {
  @Override
  public WebhookEvent parse(String body) {
    // У Stripe тело — JSON с полем "type"; здесь упрощённо.
    return new WebhookEvent("stripe.payment_intent.succeeded", body);
  }
}
