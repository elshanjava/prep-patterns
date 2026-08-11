package streams;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

/**
 * Java Streams — тренажёр для лайв-кодинга (40 задач).
 * Один самодостаточный файл: доменные POJO + все решения + демо в main().
 *
 *   javac StreamTasksDemo.java
 *   java  StreamTasksDemo
 *
 * Геттеры названы ровно как в документе: getSalary(), isActive(), findEmail() и т.д.
 * Деньги — BigDecimal.  Требуется JDK 16+ (используются toList(), teeing, локальные record).
 */
public class StreamTasksDemo {

    // ==================== ДОМЕН ====================

    static final class Employee {
        private final long id;
        private final String name;
        private final String dept;
        private final String city;
        private final BigDecimal salary;
        private final String email;   // может быть null — намеренно, для ловушек
        private final boolean active;
        private final int age;
        private final List<String> skills;

        Employee(long id, String name, String dept, String city, BigDecimal salary,
                 String email, boolean active, int age, List<String> skills) {
            this.id = id; this.name = name; this.dept = dept; this.city = city;
            this.salary = salary; this.email = email; this.active = active;
            this.age = age; this.skills = skills;
        }

        public long getId()            { return id; }
        public String getName()        { return name; }
        public String getDept()        { return dept; }
        public String getCity()        { return city; }
        public BigDecimal getSalary()  { return salary; }
        public String getEmail()       { return email; }
        public boolean isActive()      { return active; }
        public int getAge()            { return age; }
        public List<String> getSkills(){ return skills; }
        public Optional<String> findEmail() { return Optional.ofNullable(email); }

        @Override public String toString() { return name + "(" + dept + ")"; }
    }

    static final class Txn {
        private final String account;
        private final BigDecimal amount;
        private final boolean suspicious;

        Txn(String account, BigDecimal amount, boolean suspicious) {
            this.account = account; this.amount = amount; this.suspicious = suspicious;
        }
        public String getAccount()     { return account; }
        public BigDecimal getAmount()  { return amount; }
        public boolean isSuspicious()  { return suspicious; }

        @Override public String toString() { return account + ":" + amount; }
    }

    static final class LineItem {
        private final BigDecimal price;
        private final int qty;
        LineItem(BigDecimal price, int qty) { this.price = price; this.qty = qty; }
        public BigDecimal getPrice() { return price; }
        public int getQty()          { return qty; }
    }

    static final class Order {
        private final List<LineItem> items;
        Order(List<LineItem> items) { this.items = items; }
        public List<LineItem> getItems() { return items; }
    }

    // ==================== СЛОЙ 0. БАЗА ====================

    /** 1. Имена сотрудников. */
    static List<String> task01(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getName)
                .toList();                          // Java 16+, неизменяемый
    }

    /** 2. Число активных. */
    static long task02(List<Employee> emps) {
        return emps.stream()
                .filter(Employee::isActive)
                .count();
    }

    /** 3. Уникальные департаменты. */
    static List<String> task03(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getDept)
                .distinct()
                .toList();
    }

    /** 4. Сортировка по зарплате, затем имени. */
    static List<Employee> task04(List<Employee> emps) {
        return emps.stream()
                .sorted(Comparator.comparing(Employee::getSalary)
                        .thenComparing(Employee::getName))
                .toList();
    }

    /** 5. Пагинация. */
    static List<Employee> task05(List<Employee> emps, int page, int size) {
        return emps.stream()
                .sorted(Comparator.comparing(Employee::getId))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    /** 6. Проверки match. На пустом стриме: all=true, any=false, none=true. */
    static void task06(List<Employee> emps) {
        boolean anyFin  = emps.stream().anyMatch(e -> "FIN".equals(e.getDept()));
        boolean allMail = emps.stream().allMatch(e -> e.getEmail() != null);
        boolean noNeg   = emps.stream().noneMatch(e -> e.getSalary().signum() < 0);
        System.out.println("06 anyFin=" + anyFin + " allMail=" + allMail + " noNeg=" + noNeg);
    }

    // ==================== СЛОЙ 1. КОЛЛЕКТОРЫ ====================

    /** 7. Группировка по департаменту. */
    static Map<String, List<Employee>> task07(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept));
    }

    /** 8. Число сотрудников по департаменту. */
    static Map<String, Long> task08(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept, Collectors.counting()));
    }

    /** 9. Сумма зарплат по департаменту — ДЕНЬГИ, BigDecimal. */
    static Map<String, BigDecimal> task09(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        Collectors.reducing(BigDecimal.ZERO,
                                Employee::getSalary, BigDecimal::add)));
    }

    /** 10. Map id -> employee, last-wins на дублях. */
    static Map<Long, Employee> task10(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity(),
                        (a, b) -> b));
    }

    /** 11. Разбиение транзакций suspicious/clean + счётчик части. */
    static Map<Boolean, Long> task11(List<Txn> txns) {
        return txns.stream()
                .collect(Collectors.partitioningBy(Txn::isSuspicious, Collectors.counting()));
    }

    /** 12. Склейка имён в строку. */
    static String task12(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getName)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /** 13. Статистика по зарплатам за один проход. */
    static DoubleSummaryStatistics task13(List<Employee> emps) {
        return emps.stream()
                .mapToDouble(e -> e.getSalary().doubleValue())
                .summaryStatistics();
    }

    /** 14. Средняя зарплата за ОДИН проход через teeing (Java 12). */
    static BigDecimal task14(List<Employee> emps) {
        return emps.stream().collect(Collectors.teeing(
                Collectors.reducing(BigDecimal.ZERO, Employee::getSalary, BigDecimal::add),
                Collectors.counting(),
                (sum, c) -> c == 0 ? BigDecimal.ZERO
                        : sum.divide(BigDecimal.valueOf(c), 2, RoundingMode.HALF_EVEN)));
    }

    /** 15. Двухуровневая группировка dept -> city -> count. */
    static Map<String, Map<String, Long>> task15(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        Collectors.groupingBy(Employee::getCity, Collectors.counting())));
    }

    /** 16a. flatMapping: навыки по департаменту. */
    static Map<String, Set<String>> task16a(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        Collectors.flatMapping(e -> e.getSkills().stream(), Collectors.toSet())));
    }

    /** 16b. filtering: только активные внутри группы (пустые группы сохраняются). */
    static Map<String, List<Employee>> task16b(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        Collectors.filtering(Employee::isActive, Collectors.toList())));
    }

    // ==================== СЛОЙ 2. ПРОДВИНУТЫЙ groupingBy + КАСТОМ ====================

    /** 17. Top-3 транзакции по счёту (top-N внутри группы). */
    static Map<String, List<Txn>> task17(List<Txn> txns) {
        return txns.stream()
                .collect(Collectors.groupingBy(Txn::getAccount,
                        Collectors.collectingAndThen(Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Txn::getAmount).reversed())
                                        .limit(3)
                                        .toList())));
    }

    /** 18. Отсортированный результат группировки (TreeMap). */
    static Map<String, Long> task18(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept,
                        TreeMap::new, Collectors.counting()));
    }

    /** 19. Департамент с максимальным числом сотрудников. */
    static Optional<String> task19(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /** 20. Кастомный коллектор: медиана. Все 4 части, combiner обязателен. */
    static Collector<Integer, ?, Double> medianCollector() {
        return Collector.<Integer, List<Integer>, Double>of(
                ArrayList::new,                                    // supplier
                List::add,                                         // accumulator
                (a, b) -> { a.addAll(b); return a; },              // combiner
                list -> {                                          // finisher
                    Collections.sort(list);
                    int n = list.size();
                    if (n == 0) return 0.0;
                    return n % 2 == 1
                            ? (double) list.get(n / 2)
                            : (list.get(n / 2 - 1) + list.get(n / 2)) / 2.0;
                });
    }
    static double task20(List<Employee> emps) {
        return emps.stream().map(Employee::getAge).collect(medianCollector());
    }

    /** 21. Группировка по составному ключу (dept, active) через локальный record. */
    static Map<?, Long> task21(List<Employee> emps) {
        record Key(String dept, boolean active) {}
        return emps.stream()
                .collect(Collectors.groupingBy(
                        e -> new Key(e.getDept(), e.isActive()), Collectors.counting()));
    }

    // ==================== СЛОЙ 3. reduce + ПАРАЛЛЕЛЬНОСТЬ ====================

    /** 22. Сумма зарплат через reduce. */
    static BigDecimal task22(List<Employee> emps) {
        return emps.stream()
                .map(Employee::getSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 23. reduce с 3 аргументами (тип аккумулятора != тип элемента). */
    static int task23(List<Employee> emps) {
        return emps.stream()
                .reduce(0,
                        (part, e) -> part + e.getAge(),   // accumulator
                        Integer::sum);                     // combiner
    }

    /** 24. Ассоциативность: вычитание в parallel даёт другой ответ. */
    static int[] task24() {
        int seq = Stream.of(1, 2, 3, 4).reduce(0, (a, b) -> a - b);
        int par = Stream.of(1, 2, 3, 4).parallel().reduce(0, (a, b) -> a - b);
        return new int[]{seq, par};
    }

    /** 25. Гонка на shared mutable state — плохо и хорошо. */
    static List<String> task25(List<Employee> emps) {
        // ❌ ПЛОХО — гонка (демонстрационно, под try/catch чтобы не ронять демо)
        List<String> bad = new ArrayList<>();
        try {
            emps.parallelStream().forEach(e -> bad.add(e.getName()));
        } catch (Throwable ignore) { /* возможен ConcurrentModificationException */ }

        // ✅ ХОРОШО — коллектор потокобезопасен
        return emps.parallelStream()
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    /** 26. findFirst vs findAny. */
    static void task26(List<Employee> emps) {
        Optional<Employee> f = emps.parallelStream().filter(Employee::isActive).findFirst();
        Optional<Employee> a = emps.parallelStream().filter(Employee::isActive).findAny();
        System.out.println("26 findFirst=" + f.orElse(null) + " findAny=" + a.orElse(null));
    }

    // ==================== СЛОЙ 4. flatMap ====================

    /** 27. Выручка по всем заказам (вложенный flatMap). */
    static BigDecimal task27(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 28. Собрать непустые Optional (Optional::stream). */
    static List<String> task28(List<Employee> emps) {
        return emps.stream()
                .map(Employee::findEmail)
                .flatMap(Optional::stream)
                .toList();
    }

    /** 29. Декартово произведение account x currency. */
    static List<String> task29(List<String> accounts, List<String> currencies) {
        return accounts.stream()
                .flatMap(a -> currencies.stream().map(c -> a + ":" + c))
                .toList();
    }

    /** 30. Плоский список из Map<K, List<V>>. */
    static List<String> task30(Map<String, List<String>> deptToNames) {
        return deptToNames.entrySet().stream()
                .flatMap(e -> e.getValue().stream())
                .toList();
    }

    // ==================== СЛОЙ 5. БЕСКОНЕЧНЫЕ + СОСТОЯНИЕ ====================

    /** 31. Первые 10 чисел Фибоначчи. */
    static List<Long> task31() {
        return Stream.iterate(new long[]{0, 1}, f -> new long[]{f[1], f[0] + f[1]})
                .limit(10)
                .map(f -> f[0])
                .toList();
    }

    /** 32. Степени двойки до 1000 (iterate 3-арг). */
    static List<Integer> task32() {
        return Stream.iterate(1, n -> n <= 1000, n -> n * 2).toList();
    }

    /** 33. takeWhile / dropWhile. */
    static List<List<Integer>> task33(List<Integer> nums) {
        List<Integer> head = nums.stream().takeWhile(n -> n < 100).toList();
        List<Integer> tail = nums.stream().dropWhile(n -> n < 100).toList();
        return List.of(head, tail);
    }

    /** 34. Running balance — stateful/sequential, стрим НЕ нужен. */
    static List<BigDecimal> task34(List<Txn> txns) {
        BigDecimal bal = BigDecimal.ZERO;
        List<BigDecimal> running = new ArrayList<>();
        for (Txn t : txns) {
            bal = bal.add(t.getAmount());
            running.add(bal);
        }
        return running;
    }

    // ==================== СЛОЙ 6. ЛОВУШКИ СЕМАНТИКИ ====================

    /** 35. Повторное использование стрима -> IllegalStateException. */
    static void task35(List<Employee> emps) {
        Stream<Employee> s = emps.stream();
        s.count();
        try {
            s.findFirst();  // ❌ stream has already been operated upon
        } catch (IllegalStateException ex) {
            System.out.println("35 поймали: " + ex.getMessage());
        }
    }

    /** 36. peek может не выполниться при count() с известным размером. */
    static long task36() {
        return Stream.of("a", "b", "c")
                .peek(x -> System.out.println("36 peek: " + x))  // может НЕ печатать
                .count();
    }

    /** 37. Ленивость: без терминала пайплайн не исполняется (вывода нет). */
    static void task37(List<Employee> emps) {
        emps.stream()
                .filter(e -> { System.out.println("37 check " + e); return e.isActive(); });
        System.out.println("37 (выше ничего не напечаталось — нет терминала)");
    }

    /** 38. reversed() разворачивает ВСЮ цепочку — правильный vs неправильный. */
    static List<Employee> task38(List<Employee> emps) {
        // ✅ dept по возрастанию, salary по убыванию
        return emps.stream()
                .sorted(Comparator.comparing(Employee::getDept)
                        .thenComparing(Comparator.comparing(Employee::getSalary).reversed()))
                .toList();
    }

    /** 39. Collectors.toList() (mutable) vs Stream.toList() (immutable). */
    static void task39(List<Employee> emps) {
        List<String> mutable   = emps.stream().map(Employee::getName).collect(Collectors.toList());
        List<String> immutable = emps.stream().map(Employee::getName).toList();
        mutable.add("OK-mutable");
        try {
            immutable.add("x");  // ❌ UnsupportedOperationException
        } catch (UnsupportedOperationException ex) {
            System.out.println("39 immutable.add -> UnsupportedOperationException");
        }
    }

    /** 40. toMap падает на null-значении; groupingBy — нет. */
    static void task40(List<Employee> emps) {
        try {
            Map<Long, String> m = emps.stream()
                    .collect(Collectors.toMap(Employee::getId, Employee::getEmail)); // NPE если email==null
            System.out.println("40 без null: " + m.size());
        } catch (NullPointerException ex) {
            System.out.println("40 toMap упал на null email (ожидаемо)");
        }
        // безопасно: отфильтровать заранее
        Map<Long, String> safe = emps.stream()
                .filter(e -> e.getEmail() != null)
                .collect(Collectors.toMap(Employee::getId, Employee::getEmail));
        System.out.println("40 safe size=" + safe.size());
    }

    // ==================== ДЕМО ====================

    public static void main(String[] args) {
        List<Employee> emps = List.of(
                new Employee(1,  "Anna",   "FIN",  "Baku",      new BigDecimal("5200.00"), "anna@x.io",   true,  34, List.of("Java","Kafka")),
                new Employee(2,  "Boris",  "FIN",  "Baku",      new BigDecimal("6100.50"), null,           true,  41, List.of("Java","SQL")),
                new Employee(3,  "Cara",   "ENG",  "Berlin",    new BigDecimal("7300.00"), "cara@x.io",   false, 29, List.of("Go","K8s")),
                new Employee(4,  "Dmitry", "ENG",  "Berlin",    new BigDecimal("7300.00"), "dmitry@x.io",  true,  38, List.of("Java","K8s","SQL")),
                new Employee(5,  "Elena",  "OPS",  "Baku",      new BigDecimal("4100.25"), "elena@x.io",   true,  47, List.of("Excel")),
                new Employee(6,  "Farid",  "FIN",  "Amsterdam", new BigDecimal("6800.00"), "farid@x.io",   true,  36, List.of("Java","Kafka","SQL")),
                new Employee(7,  "Gita",   "ENG",  "Amsterdam", new BigDecimal("8100.00"), null,           true,  44, List.of("Scala","K8s")),
                new Employee(8,  "Hasan",  "RISK", "Baku",      new BigDecimal("5500.00"), "hasan@x.io",   true,  31, List.of("Python","SQL")),
                new Employee(9,  "Irina",  "RISK", "Berlin",    new BigDecimal("5900.00"), "irina@x.io",  false, 39, List.of("Python","ML")),
                new Employee(10, "Javid",  "DATA", "Lisbon",    new BigDecimal("6400.00"), "javid@x.io",   true,  27, List.of("Python","Spark")),
                new Employee(11, "Kamran", "DATA", "Lisbon",    new BigDecimal("6400.00"), null,          false, 33, List.of("Spark","SQL")),
                new Employee(12, "Lala",   "OPS",  "Amsterdam", new BigDecimal("4300.75"), "lala@x.io",    true,  52, List.of("Excel","SQL")),
                new Employee(13, "Murad",  "ENG",  "Baku",      new BigDecimal("7000.00"), "murad@x.io",   true,  45, List.of("Java","Go")),
                new Employee(14, "Nina",   "FIN",  "Berlin",    new BigDecimal("5200.00"), "nina@x.io",    true,  30, List.of("Java","Kafka","ML"))
        );
        List<Txn> txns = List.of(
                new Txn("ACC-1", new BigDecimal("120.00"),  false),
                new Txn("ACC-1", new BigDecimal("980.00"),  true),
                new Txn("ACC-1", new BigDecimal("35.00"),   false),
                new Txn("ACC-1", new BigDecimal("450.00"),  false),
                new Txn("ACC-1", new BigDecimal("1200.00"), true),
                new Txn("ACC-2", new BigDecimal("15.00"),   false),
                new Txn("ACC-2", new BigDecimal("9000.00"), true),
                new Txn("ACC-2", new BigDecimal("250.00"),  false),
                new Txn("ACC-3", new BigDecimal("500.00"),  false),
                new Txn("ACC-3", new BigDecimal("640.00"),  false),
                new Txn("ACC-3", new BigDecimal("300.00"),  false),
                new Txn("ACC-3", new BigDecimal("90.00"),   true)
        );
        List<Order> orders = List.of(
                new Order(List.of(new LineItem(new BigDecimal("10.00"), 3), new LineItem(new BigDecimal("2.50"), 4))),
                new Order(List.of(new LineItem(new BigDecimal("100.00"), 1))),
                new Order(List.of(new LineItem(new BigDecimal("49.99"), 2), new LineItem(new BigDecimal("5.00"), 10),
                                  new LineItem(new BigDecimal("999.00"), 1)))
        );

        System.out.println("01 " + task01(emps));
        System.out.println("02 active=" + task02(emps));
        System.out.println("03 " + task03(emps));
        System.out.println("04 " + task04(emps));
        System.out.println("05 page1/size2 " + task05(emps, 1, 2));
        task06(emps);
        System.out.println("07 keys " + task07(emps).keySet());
        System.out.println("08 " + task08(emps));
        System.out.println("09 " + task09(emps));
        System.out.println("10 keys " + task10(emps).keySet());
        System.out.println("11 " + task11(txns));
        System.out.println("12 " + task12(emps));
        DoubleSummaryStatistics st = task13(emps);
        System.out.printf("13 min=%.2f max=%.2f avg=%.2f%n", st.getMin(), st.getMax(), st.getAverage());
        System.out.println("14 avgSalary=" + task14(emps));
        System.out.println("15 " + task15(emps));
        System.out.println("16a " + task16a(emps));
        System.out.println("16b keys " + task16b(emps).keySet());
        System.out.println("17 " + task17(txns));
        System.out.println("18 (TreeMap) " + task18(emps));
        System.out.println("19 biggestDept=" + task19(emps).orElse("-"));
        System.out.println("20 medianAge=" + task20(emps));
        System.out.println("21 " + task21(emps));
        System.out.println("22 totalSalary=" + task22(emps));
        System.out.println("23 totalAge=" + task23(emps));
        int[] r24 = task24();
        System.out.println("24 seq=" + r24[0] + " par=" + r24[1] + "  <- разные из-за неассоциативности");
        System.out.println("25 good=" + task25(emps));
        task26(emps);
        System.out.println("27 revenue=" + task27(orders));
        System.out.println("28 emails=" + task28(emps));
        System.out.println("29 " + task29(List.of("A", "B"), List.of("USD", "EUR")));
        System.out.println("30 " + task30(Map.of("FIN", List.of("Anna", "Boris"), "ENG", List.of("Cara"))));
        System.out.println("31 fib=" + task31());
        System.out.println("32 pow2=" + task32());
        System.out.println("33 " + task33(List.of(10, 50, 99, 100, 5, 200)));
        System.out.println("34 running=" + task34(txns));
        task35(emps);
        System.out.println("36 count=" + task36());
        task37(emps);
        System.out.println("38 " + task38(emps));
        task39(emps);
        task40(emps);
        System.out.println("\n--- все 40 отработали ---");
    }
}
