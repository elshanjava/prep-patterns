package structural.decorator.good;

import structural.decorator.model.Account;

final class LoggingRepository extends RepositoryDecorator {
    LoggingRepository(Repository delegate) { super(delegate); }

    @Override
    public Account find(String id) {
        System.out.println("  [log] find(" + id + ")");
        long t0     = System.nanoTime();
        Account acc = delegate.find(id);
        System.out.printf("  [log] find(%s) done in %d µs%n", id, (System.nanoTime() - t0) / 1_000);
        return acc;
    }
}
