package structural.decorator.good;

public class DecoratorDemo {
    public static void main(String[] args) {
        System.out.println("== Decorator [GOOD] — наращивание поведения композицией, не наследованием ==");

        // Logging → Caching → DB: порядок задаётся в runtime
        Repository repo = new LoggingRepository(
                              new CachingRepository(
                                  new DbRepository()));

        System.out.println("\n1st call (cache miss):");
        System.out.println("  result: " + repo.find("42"));

        System.out.println("\n2nd call (cache hit):");
        System.out.println("  result: " + repo.find("42"));

        System.out.println("\n--- Только кэш, без логирования ---");
        Repository noLog = new CachingRepository(new DbRepository());
        noLog.find("99");
        noLog.find("99");

        System.out.println();
        System.out.println("Преимущества над bad:");
        System.out.println("  - нет взрыва классов: Caching+Logging+DB — 3 класса, не отдельный на комбинацию");
        System.out.println("  - порядок декораторов меняется в runtime — не переписывая иерархию");
        System.out.println("  - добавить rate-limiting: RateLimitRepository extends RepositoryDecorator");
        System.out.println("  - каждый декоратор тестируется с mock-делегатом в полной изоляции");
    }
}
