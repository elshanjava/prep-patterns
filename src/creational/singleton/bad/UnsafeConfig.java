package creational.singleton.bad;

import java.util.concurrent.atomic.AtomicInteger;

// Сломанный lazy singleton: нет volatile, нет синхронизации.
// Под нагрузкой два потока увидят instance == null одновременно
// и оба сделают new — в JVM окажется два «одиночки» с разными конфигами.
// Ещё хуже: без volatile JIT вправе опубликовать ссылку ДО завершения конструктора,
// и второй поток получит частично инициализированный объект (apiKey == null).
final class UnsafeConfig {
    private static UnsafeConfig instance;     // НУЖЕН volatile — его нет
    private static final AtomicInteger constructorCount = new AtomicInteger();

    final String apiUrl;
    final int    timeoutMs;
    final String apiKey;
    final int    instanceId;   // уникальный номер экземпляра — должен быть один

    private UnsafeConfig() {
        instanceId = constructorCount.incrementAndGet();
        // имитируем задержку инициализации (реальная загрузка из remote config)
        try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        this.apiUrl    = System.getProperty("api.url",  "https://payments.example.com");
        this.timeoutMs = Integer.parseInt(System.getProperty("timeout", "5000"));
        this.apiKey    = System.getProperty("api.key",  "sk_live_" + instanceId);  // разный у каждого!
        System.out.println("  [UnsafeConfig #" + instanceId + "] создан потоком: "
                           + Thread.currentThread().getName());
    }

    // Race condition: Thread-A и Thread-B оба видят null, оба входят в if,
    // оба делают new — два разных экземпляра с разными instanceId и разными apiKey.
    static UnsafeConfig getInstance() {
        if (instance == null) {                     // ← нет лока, нет volatile
            instance = new UnsafeConfig();          // ← может выполниться дважды
        }
        return instance;
    }

    static int totalCreated() { return constructorCount.get(); }
}
