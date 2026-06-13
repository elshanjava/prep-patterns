package creational.singleton.good;

// DCL (Double-Checked Locking) с volatile: явный, читаемый контроль.
// volatile гарантирует: второй поток видит полностью сконструированный объект,
// а не частично инициализированный из-за reordering JIT/CPU.
// Без volatile DCL — сломан даже на современных JVM.
final class Config3 {
    private static volatile Config3 instance;   // volatile обязателен для корректного DCL

    private final String apiUrl;
    private final int    timeoutMs;
    private final String apiKey;

    private Config3() {
        this.apiUrl    = System.getProperty("api.url",  "https://payments.example.com");
        this.timeoutMs = Integer.parseInt(System.getProperty("timeout", "5000"));
        this.apiKey    = System.getProperty("api.key",  "sk_live_default");
        System.out.println("  [Config3-DCL] created by: " + Thread.currentThread().getName());
    }

    static Config3 getInstance() {
        if (instance == null) {                 // первая проверка без лока (быстрый путь)
            synchronized (Config3.class) {
                if (instance == null) {         // вторая проверка под локом
                    instance = new Config3();
                }
            }
        }
        return instance;
    }

    String apiUrl()    { return apiUrl; }
    int    timeoutMs() { return timeoutMs; }
    String apiKey()    { return apiKey; }
}
