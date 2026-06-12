package structural.proxy;

// Заглушка контекста безопасности. Для демо: счета с префиксом "secret"
// читать запрещено — на этом прокси показывает контроль доступа.
final class SecurityContext {
    static boolean canRead(String id) {
        return !id.startsWith("secret");
    }
}
