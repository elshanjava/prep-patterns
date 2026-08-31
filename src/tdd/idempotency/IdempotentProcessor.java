package tdd.idempotency;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class IdempotentProcessor {

    // Одна мапа: Future играет роль И кэша, И "замка".
    // Первый поток кладёт FutureTask через putIfAbsent и запускает его;
    // конкурентные потоки получают ТОТ ЖЕ Future и ждут результат на get().
    // exactly-once достаётся бесплатно, а само действие выполняется ВНЕ лока мапы.
    private final ConcurrentHashMap<String, Future<Object>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T process(String requestId, Callable<T> action) throws Exception {
        Future<Object> future = cache.get(requestId);
        if (future == null) {
            FutureTask<Object> task = new FutureTask<>(action::call);
            future = cache.putIfAbsent(requestId, task);   // атомарная гонка: кто первый — тот вставил
            if (future == null) {                          // мы выиграли → нам и выполнять
                future = task;
                task.run();                                // выполняется здесь, лок мапы не держим
            }
        }

        try {
            return (T) future.get();                       // опоздавшие ждут первого тут
        } catch (ExecutionException e) {
            cache.remove(requestId, future);               // упавшее действие НЕ кэшируем — повтор возможен
            Throwable cause = e.getCause();
            if (cause instanceof Exception ex) throw ex;
            throw e;
        }
    }
}
