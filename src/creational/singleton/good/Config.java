package creational.singleton.good;

// Initialization-on-demand holder: JVM гарантирует однократную загрузку
// внутреннего класса — без volatile, без synchronized на горячем пути.
// Лучший вариант для большинства случаев.
final class Config {
    private final String apiUrl;
    private final int    timeoutMs;
    private final String apiKey;

    private Config() {
        this.apiUrl    = System.getProperty("api.url",  "https://payments.example.com");
        this.timeoutMs = Integer.parseInt(System.getProperty("timeout", "5000"));
        this.apiKey    = System.getProperty("api.key",  "sk_live_default");
        System.out.println("  [Config-Holder] created by: " + Thread.currentThread().getName());
    }

    private static final class Holder {
        static final Config INSTANCE = new Config();
    }

    static Config getInstance() { return Holder.INSTANCE; }

    String apiUrl()    { return apiUrl; }
    int    timeoutMs() { return timeoutMs; }
    String apiKey()    { return apiKey; }
}
