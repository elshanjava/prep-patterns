package tdd.urlshortener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UrlShortener {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final ConcurrentHashMap<String, String> urlToCode = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> codeToUrl = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    public String shorten(String url) {
        // computeIfAbsent атомарен в ConcurrentHashMap — нет дубликатов под нагрузкой
        return urlToCode.computeIfAbsent(url, u -> {
            String code = encode(counter.getAndIncrement());
            codeToUrl.put(code, u);
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
