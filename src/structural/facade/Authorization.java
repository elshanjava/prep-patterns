package structural.facade;

// Результат авторизации в PSP — внутренний объект подсистемы.
public record Authorization(String id, long amountCents) {}
