package coding.creational.singleton;

public enum SingletonExampleEnum {
    INSTANCE;

    private final String name1 = System.getProperty("user.name");
    private final Integer age = Integer.valueOf(System.getProperty("user.age"));

    String name1() {return name1;}
    Integer age() {return age;}
}
