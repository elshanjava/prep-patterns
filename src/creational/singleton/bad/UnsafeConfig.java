package creational.singleton.bad;

// Сломанный lazy singleton: нет volatile, нет синхронизации.
// Под параллельной нагрузкой два потока могут одновременно увидеть instance == null
// и оба создадут свой экземпляр — в приложении будет два «одиночки» с разным состоянием.
// Ещё хуже: без volatile JIT вправе переупорядочить инициализацию полей,
// и второй поток получит ЧАСТИЧНО сконструированный объект.
final class UnsafeConfig {
    private static UnsafeConfig instance;   // НУЖЕН volatile — его нет

    private final String apiUrl;
    private final int    timeoutMs;
    private final String apiKey;

    private UnsafeConfig() {
        this.apiUrl    = System.getProperty("api.url",  "https://payments.example.com");
        this.timeoutMs = Integer.parseInt(System.getProperty("timeout", "5000"));
        this.apiKey    = System.getProperty("api.key",  "sk_live_default");
        System.out.println("  [UnsafeConfig] new instance created by thread: "
                           + Thread.currentThread().getName());
    }

    // Race condition: Thread A и Thread B оба видят null, оба входят в if,
    // оба делают new — получаем два разных экземпляра.
    public static UnsafeConfig getInstance() {
        if (instance == null) {                         // ← нет лока, нет volatile
            instance = new UnsafeConfig();              // ← может выполниться дважды
        }
        return instance;
    }

    public String apiUrl()    { return apiUrl; }
    public int    timeoutMs() { return timeoutMs; }
    public String apiKey()    { return apiKey; }
}
