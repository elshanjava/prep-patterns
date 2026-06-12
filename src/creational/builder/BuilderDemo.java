package creational.builder;

// Запуск: пошаговая сборка иммутабельного объекта + единая валидация в build().
public class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("== Builder ==");

        Transfer t = Transfer.builder()
                .from("ACC-A").to("ACC-B")
                .amount(100)
                .reference("inv-42")        // опциональные поля можно опускать
                .build();
        System.out.println("created: " + t);

        // Невалидные комбинации отсекаются в одной точке — до создания объекта:
        try {
            Transfer.builder().from("A").amount(-5).build();   // нет to + amount<=0
        } catch (IllegalStateException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}
