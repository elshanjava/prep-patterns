package behavioral.interpreter.bad;

import behavioral.interpreter.model.Tx;

import java.math.BigDecimal;
import java.util.List;

// Грамматика разбирается вручную строковыми операциями.
// Нельзя вложить скобки, OR не работает (молчаливый пропуск),
// добавить оператор = переписать весь парсер.
final class BadRuleChecker {

    // Только AND работает. OR → молча пропускается как «unknown».
    // Вложенные скобки не поддерживаются вообще.
    boolean check(String rule, Tx tx) {
        for (String part : rule.split(" AND ")) {
            part = part.trim();
            if (part.startsWith("amount>")) {
                var limit = new BigDecimal(part.substring(7).trim());
                if (tx.amount().compareTo(limit) <= 0) return false;
            } else if (part.startsWith("amount<")) {
                var limit = new BigDecimal(part.substring(7).trim());
                if (tx.amount().compareTo(limit) >= 0) return false;
            } else if (part.startsWith("country==")) {
                var code = part.substring(9).trim();
                if (!tx.country().equals(code)) return false;
            }
            // "OR" — молча игнорируется: результат неверен, ошибки нет
            // Скобки — не поддерживаются: "(amount>500 AND country==DE) OR country==CN" сломается
        }
        return true;
    }

    // Батч-проверка: то же строковое ручное разбивание скопировано для каждого правила
    // Нет возможности переиспользовать разобранное правило между вызовами (нет AST)
    List<Boolean> checkBatch(String rule, List<Tx> transactions) {
        var results = new java.util.ArrayList<Boolean>();
        for (Tx tx : transactions) {
            // дублирование разбора строки для каждой транзакции — нет кэша/компиляции
            boolean match = true;
            for (String part : rule.split(" AND ")) {
                part = part.trim();
                if (part.startsWith("amount>")) {
                    var limit = new BigDecimal(part.substring(7).trim());
                    if (tx.amount().compareTo(limit) <= 0) { match = false; break; }
                } else if (part.startsWith("country==")) {
                    var code = part.substring(9).trim();
                    if (!tx.country().equals(code)) { match = false; break; }
                }
                // OR снова игнорируется
            }
            results.add(match);
        }
        return results;
    }
}
