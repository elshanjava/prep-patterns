package tdd;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortener {

    private final Map<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final Map<String, String> urlToCode = new ConcurrentHashMap<>();
    private final Random rnd = new SecureRandom();

    public String shorten(String url) {
        Objects.requireNonNull(url, "url must not be null");
        return urlToCode.computeIfAbsent(url, u -> {
            String code = generateCode();
            codeToUrl.put(code, u);
            return code;
        });
    }


    public String resolve(String code) {
        String url = codeToUrl.get(code);
        if (url == null) throw new IllegalArgumentException("unknown code: " + code);
        return url;
    }

    String generateCode() {
        return Long.toString(Math.abs(rnd.nextLong()), 36);
    }
}
