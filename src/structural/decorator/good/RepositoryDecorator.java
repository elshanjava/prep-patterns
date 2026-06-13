package structural.decorator.good;

import structural.decorator.model.Account;

// Базовый декоратор: реализует Repository, делегирует дальше.
// Подклассы переопределяют find() и добавляют одно конкретное поведение.
abstract class RepositoryDecorator implements Repository {
    protected final Repository delegate;

    RepositoryDecorator(Repository delegate) { this.delegate = delegate; }

    @Override
    public Account find(String id) { return delegate.find(id); }
}
