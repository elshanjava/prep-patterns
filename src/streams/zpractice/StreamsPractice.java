package streams.zpractice;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

/**
 * Тренажёр по стримам с САМОПРОВЕРКОЙ.
 *
 * Как пользоваться:
 *   1. заполняешь тело task01..taskNN (сейчас возвращают null → все ❌);
 *   2. запускаешь main() (IntelliJ: ▶ или `java streams.zpractice.StreamsPractice`);
 *   3. смотришь ✅/❌ — эталонный ответ зашит в проверке, реализацию НЕ подсматриваешь
 *      в src/streams/StreamTasksDemo.java, пока не сдашься.
 *
 * Данные фиксированные (см. EMPLOYEES) — ответы детерминированы.
 */
public class StreamsPractice {

    // ==================== ДОМЕН ====================

    static final class Employee {
        private final long id;
        private final String name;
        private final String dept;
        private final String city;
        private final BigDecimal salary;
        private final String email;      // может быть null — намеренно
        private final boolean active;
        private final int age;
        private final List<String> skills;

        Employee(long id, String name, String dept, String city, BigDecimal salary,
                 String email, boolean active, int age, List<String> skills) {
            this.id = id; this.name = name; this.dept = dept; this.city = city;
            this.salary = salary; this.email = email; this.active = active;
            this.age = age; this.skills = skills;
        }
        public long getId()             { return id; }
        public String getName()         { return name; }
        public String getDept()         { return dept; }
        public String getCity()         { return city; }
        public BigDecimal getSalary()   { return salary; }
        public String getEmail()        { return email; }
        public boolean isActive()       { return active; }
        public int getAge()             { return age; }
        public List<String> getSkills() { return skills; }
        public Optional<String> findEmail() { return Optional.ofNullable(email); }
        @Override public String toString() { return name; }
    }

    // ==================== МОК-ДАННЫЕ (фиксированные) ====================

    static final List<Employee> EMPLOYEES = List.of(
        //         id  name     dept     city     salary            email          active age  skills
        new Employee(1, "Alice", "Eng",   "London", bd(100000), "alice@x.com", true,  30, List.of("java","sql")),
        new Employee(2, "Bob",   "Eng",   "London", bd( 90000), null,          true,  25, List.of("java")),
        new Employee(3, "Carol", "Sales", "Paris",  bd( 80000), "carol@x.com", false, 40, List.of("excel")),
        new Employee(4, "Dave",  "Sales", "Paris",  bd( 90000), "dave@x.com",  true,  35, List.of("excel","sql")),
        new Employee(5, "Eve",   "Eng",   "Berlin", bd(120000), null,          false, 45, List.of("java","python"))
    );

    static BigDecimal bd(long v) { return BigDecimal.valueOf(v); }

    // ==================== ЗАДАЧИ (слой 0 — заполняй) ====================

    /** 1. Список имён всех сотрудников. Ожидается: [Alice, Bob, Carol, Dave, Eve] */
    static List<String> task01(List<Employee> emps) {
        return emps.stream().map(Employee::getName).toList();
    }

    /** 2. Сколько сотрудников активны (isActive). Ожидается: 3 */
    static long task02(List<Employee> emps) {
        return emps.stream()
                .filter(Employee::isActive)
                .count();
    }

    /** 3. Уникальные департаменты в порядке встречи. Ожидается: [Eng, Sales] */
    static List<String> task03(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getDept)
                .distinct()
                .toList();

    }

    /** 4. Имена, отсортированные по зарплате (возр.), при равной — по имени.
     *     Ожидается: [Carol, Bob, Dave, Alice, Eve]  (Bob и Dave по 90k → по имени) */
    static List<String> task04(List<Employee> emps) {
        return emps.stream()
                .sorted(Comparator.comparing(Employee::getSalary)
                        .thenComparing(Employee::getName))
                .map(Employee::getName)
                .toList();
    }

    /** 5. Пагинация: страница page (0-based) размера size — вернуть имена.
     *     Для page=1, size=2 ожидается: [Carol, Dave] */
    static List<String> task05(List<Employee> emps, int page, int size) {
        return emps.stream()
                .sorted(Comparator.comparing(Employee::getId))
                .map(Employee::getName)
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    /** 6. Все ли сотрудники активны? Ожидается: false */
    static boolean task06(List<Employee> emps) {
        return emps.stream()
                .allMatch(Employee::isActive);
    }

    // ==================== ЗАДАЧИ (слой 1 — КОЛЛЕКТОРЫ) ====================

    /** 7. Число сотрудников по департаменту. Ожидается: {Eng=3, Sales=2} */
    static Map<String, Long> task07(List<Employee> emps) {
        return emps.stream()
                .collect(groupingBy(Employee::getDept, counting()));
    }

    /** 8. Имена сотрудников, сгруппированные по департаменту.
     *     Ожидается: {Eng=[Alice, Bob, Eve], Sales=[Carol, Dave]} */
    static Map<String, List<String>> task08(List<Employee> emps) {
        return emps.stream()
                .collect(groupingBy(Employee::getDept, Collectors.mapping(Employee::getName, Collectors.toList())));
    }

    /** 9. Сумма зарплат по департаменту — ДЕНЬГИ (BigDecimal).
     *     Ожидается: {Eng=310000, Sales=170000} */
    static Map<String, BigDecimal> task09(List<Employee> emps) {
        return emps.stream()
                .collect(groupingBy(Employee::getDept,
                        Collectors.reducing(BigDecimal.ZERO, Employee::getSalary, BigDecimal::add)));
    }

    /** 10. Разбить на активных/неактивных, вернуть имена.
     *      Ожидается: {false=[Carol, Eve], true=[Alice, Bob, Dave]} */
    static Map<Boolean, List<String>> task10(List<Employee> emps) {
        return emps.stream()
                .collect(partitioningBy(Employee::isActive, Collectors.mapping(Employee::getName, Collectors.toList())));
    }

    /** 11. Map id -> имя. Ожидается: {1=Alice, 2=Bob, 3=Carol, 4=Dave, 5=Eve} */
    static Map<Long, String> task11(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getName));
    }

    /** 12. Склеить имена через ", ". Ожидается: "Alice, Bob, Carol, Dave, Eve" */
    static String task12(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", "));
    }

    /** 13. Двухуровневая группировка dept -> city -> count.
     *      Ожидается: {Eng={London=2, Berlin=1}, Sales={Paris=2}} */
    static Map<String, Map<String, Long>> task13(List<Employee> emps) {
        return emps.stream()
                .collect(groupingBy(Employee::getDept, groupingBy(Employee::getCity, counting())));
    }

    // ==================== ПРОВЕРКА ====================

    public static void main(String[] args) {
        int[] score = {0, 0}; // [passed, total]

        check(score, "task01", task01(EMPLOYEES), List.of("Alice","Bob","Carol","Dave","Eve"));
        check(score, "task02", task02(EMPLOYEES), 3L);
        check(score, "task03", task03(EMPLOYEES), List.of("Eng","Sales"));
        check(score, "task04", task04(EMPLOYEES), List.of("Carol","Bob","Dave","Alice","Eve"));
        check(score, "task05", task05(EMPLOYEES, 1, 2), List.of("Carol","Dave"));
        check(score, "task06", task06(EMPLOYEES), false);

        // --- слой 1: коллекторы ---
        check(score, "task07", task07(EMPLOYEES), Map.of("Eng", 3L, "Sales", 2L));
        check(score, "task08", task08(EMPLOYEES), Map.of("Eng", List.of("Alice","Bob","Eve"), "Sales", List.of("Carol","Dave")));
        check(score, "task09", task09(EMPLOYEES), Map.of("Eng", bd(310000), "Sales", bd(170000)));
        check(score, "task10", task10(EMPLOYEES), Map.of(true, List.of("Alice","Bob","Dave"), false, List.of("Carol","Eve")));
        check(score, "task11", task11(EMPLOYEES), Map.of(1L,"Alice", 2L,"Bob", 3L,"Carol", 4L,"Dave", 5L,"Eve"));
        check(score, "task12", task12(EMPLOYEES), "Alice, Bob, Carol, Dave, Eve");
        check(score, "task13", task13(EMPLOYEES), Map.of("Eng", Map.of("London",2L,"Berlin",1L), "Sales", Map.of("Paris",2L)));

        System.out.printf("%n==== %d / %d ====%n", score[0], score[1]);
    }

    private static void check(int[] score, String name, Object actual, Object expected) {
        score[1]++;
        boolean ok = Objects.equals(actual, expected);
        if (ok) score[0]++;
        System.out.printf("%s %-8s -> %s%s%n",
                ok ? "✅" : "❌",
                name,
                actual,
                ok ? "" : "   (ожидалось: " + expected + ")");
    }
}
