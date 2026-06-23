package coding.creational.builder;

public class Main {

    public static void main(String[] args) {
        // Полный запрос — все поля заданы, читается как предложение
        HttpRequest full = HttpRequest.builder("https://api.revolut.com/payments")
                .method("POST")
                .header("Authorization: Bearer xyz")
                .build();
        System.out.println("full:   " + format(full));

        // Только обязательное поле — method/header останутся null (опциональны)
        HttpRequest minimal = HttpRequest.builder("https://api.revolut.com/health")
                .build();
        System.out.println("minimal:" + format(minimal));

        // Порядок опциональных не важен — header до method
        HttpRequest reordered = HttpRequest.builder("https://api.revolut.com/cards")
                .header("X-Trace: 123")
                .method("GET")
                .build();
        System.out.println("reorder:" + format(reordered));

        // Валидация: пустой url падает сразу в builder()
        try {
            HttpRequest.builder("").build();
        } catch (IllegalArgumentException e) {
            System.out.println("invalid: " + e.getMessage());
        }
    }

    private static String format(HttpRequest r) {
        return " " + r.getMethod() + " " + r.getUrl() + " | header=" + r.getHeader();
    }
}
