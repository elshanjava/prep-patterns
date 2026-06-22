package coding.creational.singleton;

public class SingletonEagerExample {

    private static volatile SingletonEagerExample INSTANCE;

    private final String name;
    private final Integer age;

    private SingletonEagerExample() {
        this.name = System.getProperty("user.name");
        this.age = Integer.valueOf(System.getProperty("user.age"));
    }

    public static SingletonEagerExample getInstance() {
        if (INSTANCE == null) {
            synchronized(SingletonEagerExample.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SingletonEagerExample();
                }
            }
        }
        return INSTANCE;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }
}
