package behavioral.interpreter.bad;

import behavioral.interpreter.model.Tx;

import java.math.BigDecimal;

// Грамматика разбирается вручную строковыми операциями.
// Нельзя вложить скобки, добавить оператор, тестировать условие изолированно.
final class BadRuleChecker {
    boolean check(String rule, Tx tx) {
        for (String part : rule.split(" AND ")) {
            part = part.trim();
            if (part.startsWith("amount>")) {
                var limit = new BigDecimal(part.substring(7).trim());
                if (tx.amount().compareTo(limit) <= 0) return false;
            } else if (part.startsWith("country==")) {
                var code = part.substring(9).trim();
                if (!tx.country().equals(code)) return false;
            }
            // неизвестный оператор → молчаливый пропуск
        }
        return true;
    }
}
