package tdd.zpractice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class IdempotentProcessor2 {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, Object> lock = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public  <T> T process(String s, Callable<T> action) throws Exception {
        if (cache.containsKey(s)) return (T) cache.get(s);

      Object o = lock.computeIfAbsent(s, k -> new Object());
      synchronized (o) {
        Object result1 = cache.get(s);
        if (result1 != null) return (T) result1;
        Object result = action.call();
        cache.put(s, result);
        return (T) result;
      }
    }

  private final ConcurrentHashMap<String, Future<Object>> cache1 = new ConcurrentHashMap<>();

  public <T> T process1 (String reqId, Callable<T> action) {
    Future<Object> objectFuture = cache1.get(reqId);
    if (objectFuture == null) {
        FutureTask<Object> task = new FutureTask<>(action::call);
        objectFuture = cache1.putIfAbsent(reqId, task);
        if (objectFuture == null) {
          objectFuture = task;
          task.run();
        }
      }
    try {
      return (T) objectFuture.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
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
