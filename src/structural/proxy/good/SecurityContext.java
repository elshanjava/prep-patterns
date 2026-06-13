package structural.proxy.good;

// Выделенная логика авторизации: заменяема моком в тестах.
final class SecurityContext {
    boolean isAllowed(String id) {
        return !id.startsWith("secret");
    }
}
