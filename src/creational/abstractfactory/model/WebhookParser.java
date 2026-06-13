package creational.abstractfactory.model;

public interface WebhookParser {
    WebhookEvent parse(String body);
}
