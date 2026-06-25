package coding.behavioral.chainofresponsibility;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Цепочка задаётся составом и порядком в List (как в Spring: List<Handler> + @Order).
        // Порядок: Auth -> Validation -> RateLimit -> Business.
        HandlerPipeline pipeline = new HandlerPipeline(List.of(
                new AuthHandler(),
                new ValidationHandler(),
                new RateLimitHandler(2),          // максимум 2 запроса на клиента
                new BusinessHandler(1_000_00)     // сумма > 1000.00 -> REVIEW
        ));

        // 1. Нет токена -> коротит на AuthHandler -> DECLINE
        print("без токена", pipeline.process(
                new HttpRequest(null, "client-1", 500_00)));

        // 2. Битый запрос (нет clientId) -> коротит на ValidationHandler -> DECLINE
        print("нет clientId", pipeline.process(
                new HttpRequest("tok", "", 500_00)));

        // 3. Нормальный запрос -> проходит всех -> APPROVE (дефолт в конце цепочки)
        print("нормальный", pipeline.process(
                new HttpRequest("tok", "client-A", 500_00)));

        // 4. Крупная сумма -> коротит на BusinessHandler -> REVIEW
        print("крупная сумма", pipeline.process(
                new HttpRequest("tok", "client-B", 5_000_00)));

        // 5. Rate limit: client-C шлёт 3 раза при лимите 2 -> 3-й коротит на RateLimit -> DECLINE
        System.out.println();
        System.out.println("-- rate limit (лимит 2 на клиента) --");
        for (int i = 1; i <= 3; i++) {
            print("client-C запрос #" + i, pipeline.process(
                    new HttpRequest("tok", "client-C", 100_00)));
        }
    }

    private static void print(String label, Decision decision) {
        System.out.printf("%-22s -> %s%n", label, decision);
    }
}
