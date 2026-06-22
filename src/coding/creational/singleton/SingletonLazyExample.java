package coding.creational.singleton;

public class SingletonLazyExample {

    private final String name;
    private final Integer age;

    private SingletonLazyExample() {
        this.name = System.getProperty("user.name", "test default name");
        this.age = Integer.valueOf(System.getProperty("user.age", "test default age"));
    }

    private static class SingletonHolder {
        private static final SingletonLazyExample INSTANCE = new SingletonLazyExample();
    }

    public static SingletonLazyExample getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getName() {
        return this.name;
    }

    public Integer getAge() {
        return this.age;
    }

}
