package creational.abstractfactory;

// Абстрактная фабрика: семейство согласованных продуктов
interface PspFactory {
    PspClient createClient();
    WebhookParser createWebhookParser();
}
