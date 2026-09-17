package streams.zpractice;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    // ==================== ЗАДАЧИ (слой 2 — ПРОДВИНУТЫЙ) ====================

    /** 14. Top-2 самых высокооплачиваемых имени в каждом департаменте (по убыванию зарплаты).
     *      Ожидается: {Eng=[Eve, Alice], Sales=[Dave, Carol]} */
    static Map<String, List<String>> task14(List<Employee> emps) {
        return emps.stream()
                .collect(groupingBy(Employee::getDept,
                        Collectors.collectingAndThen(toList(),  (List<Employee> list) -> list.stream()
                                        .sorted(Comparator.comparing(Employee::getSalary).reversed())
                                        .limit(2)
                                        .map(Employee::getName)
                                        .toList())));
    }

    /** 15. Департамент с наибольшим числом сотрудников. Ожидается: "Eng" */
    static String task15(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept, counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /** 16. Медиана зарплат через КАСТОМНЫЙ Collector (все 4 части: supplier/accumulator/combiner/finisher).
     *      Данные: 80k,90k,90k,100k,120k → медиана 90000. Ожидается: 90000 */
    static BigDecimal task16(List<Employee> emps) {
        return emps.stream()
                .collect(Collector.of(
                        ArrayList<BigDecimal>::new,
                        (list, e) -> list.add(e.getSalary()),
                        (a, b) -> {a.addAll(b); return a;},
                        list -> {
                            Collections.sort(list);                 // отсортировать по возрастанию
                            int n = list.size();
                            if (n == 0) return BigDecimal.ZERO;      // пусто → 0
                            if (n % 2 == 1) {                        // НЕЧЁТНОЕ → средний элемент
                                return list.get(n / 2);
                            } else {                                 // ЧЁТНОЕ → среднее двух средних
                                return list.get(n/2 - 1)
                                        .add(list.get(n/2))
                                        .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                            }
                        }

                ));
    }

    // ==================== ЗАДАЧИ (слой 3 — flatMap) ====================

    /** 17. Все навыки всех сотрудников в один плоский список (С повторами, в порядке встречи).
     *      Ожидается: [java, sql, java, excel, excel, sql, java, python] */
    static List<String> task17(List<Employee> emps) {
        return emps.stream()
                .flatMap(e-> e.getSkills().stream())
                .toList();
    }

    /** 18. Уникальные навыки (в порядке встречи).
     *      Ожидается: [java, sql, excel, python] */
    static List<String> task18(List<Employee> emps) {
        return emps.stream()
                .flatMap(e-> e.getSkills().stream())
                .distinct()
                .toList();
    }

    /** 19. Собрать НЕ-null email'ы через Optional::stream (findEmail() возвращает Optional).
     *      Ожидается: [alice@x.com, carol@x.com, dave@x.com]  (у Bob и Eve email null) */
    static List<String> task19(List<Employee> emps) {
        return emps.stream()
                .flatMap(e-> e.findEmail().stream())
                .toList();
    }

    // ==================== ЗАДАЧИ (слой 4 — reduce) ====================

    /** 20. Сумма всех зарплат через reduce (3-арг: тип аккумулятора BigDecimal != тип элемента Employee).
     *      Ожидается: 480000 */
    static BigDecimal task20(List<Employee> emps) {
        return emps.stream()
                .reduce(BigDecimal.ZERO, (acc, e)->acc.add(e.getSalary()), BigDecimal::add);
    }

    /** 21. Максимальная зарплата через reduce. Ожидается: 120000 */
    static BigDecimal task21(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal::max)
                .orElse(null);
    }

    // ==================== ЗАДАЧИ (слой 5 — ЛОВУШКИ) ====================

    /** 22. Map dept -> имя ПЕРВОГО сотрудника отдела через toMap.
     *      Ловушка: без merge-функции toMap бросает IllegalStateException на дубле ключа (два Eng).
     *      Дай merge (оставить первого). Ожидается: {Eng=Alice, Sales=Carol} */
    static Map<String, String> task22(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.toMap(Employee::getDept, Employee::getName, (a, b) -> a));
    }

    /** 23. Ловушка неизменяемости: результат Stream.toList() (Java 16) НЕизменяемый.
     *      Верни true, если попытка add() бросает UnsupportedOperationException. Ожидается: true */
    static boolean task23(List<Employee> emps) {
        List<String> names = emps.stream().map(Employee::getName).toList();
        try {
            names.add("X");
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    /** 24. Ловушка toMap + null: значение email бывает null (Bob, Eve), и toMap кидает NPE на null-значении.
     *      Верни true, если toMap(id, email) действительно бросает NPE. Ожидается: true */
    static boolean task24(List<Employee> emps) {
        try {
            emps.stream().collect(toMap(Employee::getId, Employee::getEmail));
            return false;
        } catch (NullPointerException e) {
            return true;
        }
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

        // --- слой 2: продвинутый ---
        check(score, "task14", task14(EMPLOYEES), Map.of("Eng", List.of("Eve","Alice"), "Sales", List.of("Dave","Carol")));
        check(score, "task15", task15(EMPLOYEES), "Eng");
        check(score, "task16", task16(EMPLOYEES), bd(90000));

        // --- слой 3: flatMap ---
        check(score, "task17", task17(EMPLOYEES), List.of("java","sql","java","excel","excel","sql","java","python"));
        check(score, "task18", task18(EMPLOYEES), List.of("java","sql","excel","python"));
        check(score, "task19", task19(EMPLOYEES), List.of("alice@x.com","carol@x.com","dave@x.com"));

        // --- слой 4: reduce ---
        check(score, "task20", task20(EMPLOYEES), bd(480000));
        check(score, "task21", task21(EMPLOYEES), bd(120000));

        // --- слой 5: ловушки ---
        check(score, "task22", task22(EMPLOYEES), Map.of("Eng","Alice", "Sales","Carol"));
        check(score, "task23", task23(EMPLOYEES), true);
        check(score, "task24", task24(EMPLOYEES), true);

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
