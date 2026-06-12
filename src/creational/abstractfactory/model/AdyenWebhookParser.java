package creational.abstractfactory.model;

import creational.abstractfactory.WebhookEvent;
import creational.abstractfactory.WebhookParser;

// Конкретный продукт «парсер» из семейства Adyen.
public class AdyenWebhookParser implements WebhookParser {
  @Override
  public WebhookEvent parse(String body) {
    // У Adyen свои коды событий (eventCode=AUTHORISATION и т.п.).
    return new WebhookEvent("adyen.AUTHORISATION", body);
  }
}
