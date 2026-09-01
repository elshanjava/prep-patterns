package tdd.zpractice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class IdempotentProcessor2 {

    private Map<String, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public  <T> T process(String s, Callable<T> action) throws Exception {
        if (cache.containsKey(s)) return (T) cache.get(s);
        Object result = action.call();
        cache.put(s, result);
        return (T) result;
    }

//  Ensure process(requestId, action) processes each unique requestId exactly once, even under concurrent calls.
//
//  Раскладываешь по схеме счастливый путь → вариации → ошибки → конкурентность:
//
//  1. первый вызов выполняет action и возвращает результат
//  2. тот же requestId → тот же результат, action НЕ вызывается повторно
//  3. разные requestId → выполняются оба
//  4. action упал → результат не кэшируется, повтор возможен
//  5. 50 потоков с одним requestId → ровно одно выполнение


}
