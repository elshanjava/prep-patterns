package creational.abstractfactory;

// Продукт №2 семейства: разбор вебхуков конкретного провайдера.
// У Stripe и Adyen форматы тел разные — но интерфейс для клиента один.
public interface WebhookParser {
    WebhookEvent parse(String body);
}