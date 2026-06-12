package structural.decorator;

final class LoggingRepository extends RepositoryDecorator {
    LoggingRepository(Repository delegate) { super(delegate); }
    public Account find(String id) {
        System.out.println("  [LOG] find " + id);   // поведение ДО делегата
        return delegate.find(id);
    }
}
// Собираем слои в рантайме, в любом порядке:
// Repository repo = new LoggingRepository(new CachingRepository(new DbRepository()));
