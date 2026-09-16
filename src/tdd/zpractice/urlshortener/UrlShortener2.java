package tdd.zpractice.urlshortener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UrlShortener2 {
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final AtomicInteger counter = new AtomicInteger(1);
    private final Map<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final Map<String, String> urlToCode = new ConcurrentHashMap<>();

    public String shorten(String url) {
        return urlToCode.computeIfAbsent(url, k-> {
            String code = encode(counter.getAndIncrement());
            codeToUrl.put(code, url);
            return code;
        });
    }

    public String resolve(String code) {
        String url = codeToUrl.get(code);
        if (url == null) throw new NotFoundException(code);
        return url;
    }

    private String encode(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append(ALPHABET.charAt(n % ALPHABET.length()));
            n /= ALPHABET.length();
        }
        return sb.reverse().toString();
    }
}
