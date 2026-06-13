package behavioral.chainofresponsibility.bad;

import behavioral.chainofresponsibility.model.Decision;
import behavioral.chainofresponsibility.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

// Все fraud-проверки слились в один монолитный метод.
// Нельзя переставить порядок, отключить проверку по конфигу,
// переиспользовать звено или тестировать его изолированно.
// Каждый новый бизнес-требование = разбухание этого класса.
final class FraudService {
    private static final BigDecimal LIMIT = new BigDecimal("5000");
    private final Set<String> blacklist;
    private final Set<String> flaggedUsers;
    // GEO-ограничение добавлено прямо сюда — метод растёт с каждым правилом
    private final Set<String> blockedCountries;

    FraudService(Set<String> blacklist, Set<String> flaggedUsers, Set<String> blockedCountries) {
        this.blacklist        = blacklist;
        this.flaggedUsers     = flaggedUsers;
        this.blockedCountries = blockedCountries;
    }

    // Четыре разных правила живут в одном if-каскаде.
    // Чтобы переставить порядок проверок — нужно редактировать этот метод.
    // Чтобы добавить 5-е правило — снова редактировать этот метод.
    public Decision check(Payment p) {
        if (p.amount().compareTo(LIMIT) > 0)        return Decision.DECLINE;  // 1. лимит
        if (blacklist.contains(p.card()))            return Decision.DECLINE;  // 2. блэклист карт
        if (flaggedUsers.contains(p.user()))         return Decision.REVIEW;   // 3. флаги пользователя
        if (blockedCountries.contains(p.country()))  return Decision.DECLINE;  // 4. GEO — добавление = правка метода
        return Decision.APPROVE;
    }

    // Дублирование: тот же самый if-каскад продублирован для батч-обработки.
    // Добавить новое правило → править ОБА места.
    // Забыть поправить checkBatch → молчаливое расхождение логики.
    public List<Decision> checkBatch(List<Payment> payments) {
        var results = new java.util.ArrayList<Decision>();
        for (Payment p : payments) {
            // дублирование начинается здесь ↓
            if (p.amount().compareTo(LIMIT) > 0)        { results.add(Decision.DECLINE); continue; }
            if (blacklist.contains(p.card()))            { results.add(Decision.DECLINE); continue; }
            if (flaggedUsers.contains(p.user()))         { results.add(Decision.REVIEW);  continue; }
            if (blockedCountries.contains(p.country()))  { results.add(Decision.DECLINE); continue; }
            // дублирование заканчивается здесь ↑
            results.add(Decision.APPROVE);
        }
        return results;
    }
}
