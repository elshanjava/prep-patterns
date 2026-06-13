package creational.singleton.good;

// Enum-singleton: единственный вариант, который защищён от reflection-атак
// и корректно десериализуется из ObjectInputStream.
// JLS §8.9 гарантирует однократную инициализацию.
enum Config2 {
    INSTANCE;

    private final String apiUrl    = System.getProperty("api.url",  "https://payments.example.com");
    private final int    timeoutMs = Integer.parseInt(System.getProperty("timeout", "5000"));
    private final String apiKey    = System.getProperty("api.key",  "sk_live_default");

    String apiUrl()    { return apiUrl; }
    int    timeoutMs() { return timeoutMs; }
    String apiKey()    { return apiKey; }
}
