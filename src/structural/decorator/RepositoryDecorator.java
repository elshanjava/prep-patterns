package structural.decorator;

abstract class RepositoryDecorator implements Repository {
    protected final Repository delegate;          // тот же тип, что и мы
    protected RepositoryDecorator(Repository delegate) { this.delegate = delegate; }
}
