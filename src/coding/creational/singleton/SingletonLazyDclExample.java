package coding.creational.singleton;

public class SingletonLazyDclExample {

    private static volatile SingletonLazyDclExample INSTANCE;

    private final String name;
    private final Integer age;

    private SingletonLazyDclExample() {
        this.name = System.getProperty("user.name");
        this.age = Integer.valueOf(System.getProperty("user.age"));
    }

    public static SingletonLazyDclExample getInstance() {
        if (INSTANCE == null) {
            synchronized(SingletonLazyDclExample.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SingletonLazyDclExample();
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
