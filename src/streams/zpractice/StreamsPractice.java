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

    /** Транзакция — для running balance (задача 45). */
    static final class Txn {
        private final String account;
        private final BigDecimal amount;
        private final boolean suspicious;
        Txn(String account, BigDecimal amount, boolean suspicious) {
            this.account = account; this.amount = amount; this.suspicious = suspicious;
        }
        public String getAccount()    { return account; }
        public BigDecimal getAmount() { return amount; }
        public boolean isSuspicious() { return suspicious; }
        @Override public String toString() { return account + ":" + amount; }
    }

    /** Позиция заказа — для вложенного flatMap (задача 40). */
    static final class LineItem {
        private final BigDecimal price;
        private final int qty;
        LineItem(BigDecimal price, int qty) { this.price = price; this.qty = qty; }
        public BigDecimal getPrice() { return price; }
        public int getQty()          { return qty; }
    }

    /** Заказ = список позиций. */
    static final class Order {
        private final List<LineItem> items;
        Order(List<LineItem> items) { this.items = items; }
        public List<LineItem> getItems() { return items; }
    }

    /** Составной ключ группировки (задача 37) — record как ключ мапы. */
    record DeptActive(String dept, boolean active) {}

    // ==================== МОК-ДАННЫЕ (фиксированные) ====================

    static final List<Employee> EMPLOYEES = List.of(
        //         id  name     dept     city     salary            email          active age  skills
        new Employee(1, "Alice", "Eng",   "London", bd(100000), "alice@x.com", true,  30, List.of("java","sql")),
        new Employee(2, "Bob",   "Eng",   "London", bd( 90000), null,          true,  25, List.of("java")),
        new Employee(3, "Carol", "Sales", "Paris",  bd( 80000), "carol@x.com", false, 40, List.of("excel")),
        new Employee(4, "Dave",  "Sales", "Paris",  bd( 90000), "dave@x.com",  true,  35, List.of("excel","sql")),
        new Employee(5, "Eve",   "Eng",   "Berlin", bd(120000), null,          false, 45, List.of("java","python"))
    );

    static final List<Txn> TXNS = List.of(
        new Txn("ACC-1", bd(100), false),
        new Txn("ACC-1", bd(-30), true),
        new Txn("ACC-1", bd( 50), false),
        new Txn("ACC-1", bd(-20), false)
    );

    static final List<Order> ORDERS = List.of(
        new Order(List.of(new LineItem(bd(100), 2), new LineItem(bd(50), 1))),  // 250
        new Order(List.of(new LineItem(bd(200), 3))),                           // 600
        new Order(List.of())                                                    // пустой заказ
    );

    static final List<String> ACCOUNTS   = List.of("ACC-1", "ACC-2");
    static final List<String> CURRENCIES = List.of("EUR", "USD");
    static final Map<String, List<String>> DEPT_SKILLS =
        Map.of("Eng", List.of("java", "sql"), "Sales", List.of("excel"));

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

    // ==================== ЗАДАЧИ (слой 6 — КОЛЛЕКТОРЫ, которых не было) ====================

    /** 25. Статистика по возрасту за ОДИН проход: count/sum/min/average/max.
     *      Ожидается: count=5, sum=175, min=25, max=45 (среднее 35.0). */
    static IntSummaryStatistics task25(List<Employee> emps) {
        return emps.stream()
                .collect(summarizingInt(Employee::getAge));
    }

    /** 26. Средняя зарплата через teeing (Java 12): сумма и количество за ОДИН проход.
     *      Деньги — BigDecimal, делить с scale 2 и HALF_UP.
     *      Ожидается: 96000.00 */
    static BigDecimal task26(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.teeing(
                        Collectors.reducing(BigDecimal.ZERO, Employee::getSalary, BigDecimal::add),
                        counting(),
                        (sum, count)-> sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)));
    }

    /** 27. Множество навыков по департаменту через flatMapping (Java 9),
     *      без промежуточных списков и постобработки.
     *      Ожидается: {Eng=[java, sql, python], Sales=[excel, sql]} */
    static Map<String, Set<String>> task27(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDept,flatMapping(e-> e.getSkills().stream(), toSet())));
    }

    /** 28. Имена ТОЛЬКО активных, сгруппированные по департаменту, через filtering (Java 9).
     *      Ожидается: {Eng=[Alice, Bob], Sales=[Dave]}
     *      Ловушка: с filtering пустые группы СОХРАНЯЮТСЯ; с filter() до groupingBy — исчезают. */
    static Map<String, List<String>> task28(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDept,
                                filtering(Employee::isActive, mapping(Employee::getName, toList()))));
    }

    /** 29. Счётчик по департаменту, результат отсортирован по ключу (TreeMap как mapFactory).
     *      Ожидается: {Eng=3, Sales=2} */
    static Map<String, Long> task29(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(Employee::getDept, TreeMap::new, counting()));
    }

    // ==================== ЗАДАЧИ (слой 7 — ЛОВУШКИ) ====================

    /** 30. Имена: департамент по ВОЗРАСТАНИЮ, внутри зарплата по УБЫВАНИЮ.
     *      Ожидается: [Eve, Alice, Bob, Dave, Carol]
     *      Ловушка: .thenComparing(Employee::getSalary).reversed() разворачивает ВСЮ цепочку
     *      и даёт [Dave, Carol, Eve, Alice, Bob]. reversed() вешается на ВНУТРЕННИЙ компаратор. */
    static List<String> task30(List<Employee> emps) {
        return emps.stream()
                .sorted(Comparator.comparing(
                        Employee::getDept).thenComparing(Comparator.comparing(Employee::getSalary).reversed()))
                .map(Employee::getName)
                .toList();
    }

    /** 32. Первые 10 чисел Фибоначчи через Stream.iterate с состоянием в массиве.
     *      Ожидается: [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
     *      Без limit — бесконечный стрим и зависание. */
    static List<Long> task32() {
        return Stream.iterate(new long[]{0,1}, a-> new long[]{a[1], a[0] + a[1]})
                .limit(10)
                .map(a -> a[0])
                .toList();
    }

    /** 33. Ассоциативность: верни true, если reduce(0, (a, b) -> a - b) на Stream.of(1,2,3,4)
     *      даёт РАЗНЫЙ результат последовательно и параллельно.
     *      Ожидается: true — sequential -10, parallel 0. Вычитание не ассоциативно. */
    static boolean task33() {
        int seq = Stream.of(1, 2, 3, 4).reduce(0, (a, b) -> a - b);            // 0-1-2-3-4 = -10
        int par = Stream.of(1, 2, 3, 4).parallel().reduce(0, (a, b) -> a - b); // параллельно склеивается иначе
        return seq != par;
    }


    // ==================== ЗАДАЧИ (слой 8 — ОСТАТОК ЭТАЛОНА) ====================

    /** 34. Поведение match на ПУСТОМ стриме. Вернуть [allMatch, anyMatch, noneMatch].
     *      Ожидается: [true, false, true]
     *      Отсюда реальный баг: "все записи прошли валидацию" на пустом списке — правда. */
    static List<Boolean> task34() {
        List<Employee> empty = new ArrayList<>();
        return List.of(empty.stream().allMatch(Employee::isActive),
                empty.stream().allMatch(Employee::isActive),
                empty.stream().noneMatch(Employee::isActive));
    }

    /** 35. Map dept -> имя, при дубле оставить ПОСЛЕДНЕГО (last-wins).
     *      Ожидается: {Eng=Eve, Sales=Dave}
     *      Сравни с задачей 22, где merge оставлял первого. */
    static Map<String, String> task35(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.toMap(Employee::getDept, Employee::getName, (a, b)-> b));
    }

    /** 36. findFirst на ПАРАЛЛЕЛЬНОМ стриме детерминирован: отсортируй по зарплате
     *      по убыванию и верни имя первого. Ожидается: Eve
     *      findAny на тех же данных быстрее, но может вернуть кого угодно. */
    static String task36(List<Employee> emps) {
        return emps.parallelStream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .map(Employee::getName)
                .findFirst()
                .orElseThrow();
    }

    /** 37. Группировка по СОСТАВНОМУ ключу (dept, active) через record DeptActive.
     *      Ожидается: {(Eng,true)=2, (Eng,false)=1, (Sales,true)=1, (Sales,false)=1}
     *      record бесплатно даёт equals/hashCode — без них ключ мапы не работает. */
    static Map<DeptActive, Long> task37(List<Employee> emps) {
        return emps.stream()
                .collect(Collectors.groupingBy(e -> new DeptActive(e.getDept(), e.isActive()), counting()));
    }


    /** 39. Декартово произведение ACCOUNTS x CURRENCIES в виде "ACC/CUR".
     *      Ожидается: [ACC-1/EUR, ACC-1/USD, ACC-2/EUR, ACC-2/USD] */
    static List<String> task39() {
        return ACCOUNTS.stream()
                .flatMap(acc -> CURRENCIES.stream().map(cur -> acc + "/" + cur))
                .toList();
    }

    /** 40. Выручка по ВСЕМ заказам (ORDERS): заказы -> позиции -> price * qty, сумма.
     *      Ожидается: 850  (250 + 600 + пустой заказ)
     *      Два уровня вложенности — flatMap по позициям. */
    static BigDecimal task40(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 41. Уплощение Map<String, List<String>> (DEPT_SKILLS) в пары "dept:skill", отсортированные.
     *      Ожидается: [Eng:java, Eng:sql, Sales:excel]
     *      Ключ нужен внутри flatMap — поэтому идём по entrySet(), а не по values(). */
    static List<String> task41() {
        return DEPT_SKILLS.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(skill -> e.getKey() + ":" + skill))
                .sorted()
                .toList();
    }

    /** 42. Степени двойки, не превышающие 1000, через трёхаргументный Stream.iterate (Java 9).
     *      Ожидается: [1, 2, 4, 8, 16, 32, 64, 128, 256, 512]
     *      Предикат заменяет limit — стрим конечен сам по себе. */
    static List<Integer> task42() {
        return Stream.iterate(1, n -> n < 1000, n -> n * 2).toList();
    }

    /** 43. takeWhile / dropWhile на Stream.of(1,2,3,10,4,5) с предикатом n < 10.
     *      Вернуть [результат takeWhile, результат dropWhile].
     *      Ожидается: [[1, 2, 3], [10, 4, 5]]
     *      Ловушка: filter(n -> n < 10) дал бы [1,2,3,4,5] — он не останавливается. */
    static List<List<Integer>> task43() {
        return List.of(Stream.of(1,2,3,10,4,5).takeWhile(n -> n < 10).toList(),
                Stream.of(1, 2, 3, 10, 4, 5).dropWhile(n -> n < 10).toList());
    }


    /** 45. Нарастающий остаток по TXNS: [100, -30, 50, -20] -> [100, 70, 120, 100].
     *      ПРАВИЛЬНЫЙ ОТВЕТ НА СОБЕСЕДОВАНИИ: стрим здесь не нужен. Операция stateful
     *      и последовательная; стрим потребовал бы внешнего изменяемого состояния
     *      и сломался бы в parallel. Пиши обычный цикл и проговори почему. */
    static List<BigDecimal> task45(List<Txn> txns) {
        List<BigDecimal> result = new ArrayList<>(txns.size());
        BigDecimal running = BigDecimal.ZERO;
        for (Txn t : txns) {
            running = running.add(t.getAmount());
            result.add(running);
        }
        return List.copyOf(result);
    }



    // ==================== ЛОВУШКИ: ЗНАТЬ, НЕ ПИСАТЬ ====================
    //
    // Это не задачи — писать тут нечего, ответ не пайплайн. Но спрашивают устно,
    // поэтому держим списком. Номера 31, 38, 44, 46, 47 свободны — задачи убраны отсюда.
    //
    //  peek + count()         peek может НЕ выполниться: источник List знает размер,
    //                         count() выбрасывает пайплайн целиком. Проверено — не печатает ничего.
    //
    //  ленивость              без терминальной операции промежуточные не исполняются вообще:
    //                         Stream.of(1,2,3).map(...) — функция внутри map вызвана 0 раз.
    //
    //  одноразовость          вторая терминальная операция на том же стриме ->
    //                         IllegalStateException: stream has already been operated upon or closed
    //
    //  общий ArrayList        parallel().forEach(list::add) ломается (замерено: 196 раундов из 200)
    //                         — теряет элементы, получает null, кидает исключения.
    //                         Правильно: collect(), он потокобезопасен по построению.
    //
    //  Collectors.toList()    ИЗМЕНЯЕМЫЙ ArrayList (но без гарантий на будущее) — потому его
    //                         и берут в downstream-коллекторах. Stream.toList() (16+) неизменяемый.
    //
    //  findAny vs findFirst   findAny быстрее и недетерминирован в parallel; findFirst
    //                         детерминирован, но заставляет соблюдать порядок встречи.

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


        // --- слой 6: коллекторы ---
        IntSummaryStatistics st = task25(EMPLOYEES);
        check(score, "task25", st == null ? null
                : List.of(st.getCount(), st.getSum(), st.getMin(), st.getMax()),
                List.of(5L, 175L, 25, 45));
        check(score, "task26", task26(EMPLOYEES), new BigDecimal("96000.00"));
        check(score, "task27", task27(EMPLOYEES), Map.of("Eng", Set.of("java","sql","python"),
                                                         "Sales", Set.of("excel","sql")));
        check(score, "task28", task28(EMPLOYEES), Map.of("Eng", List.of("Alice","Bob"),
                                                         "Sales", List.of("Dave")));
        check(score, "task29", task29(EMPLOYEES), Map.of("Eng", 3L, "Sales", 2L));

        // --- слой 7: ловушки ---
        check(score, "task30", task30(EMPLOYEES), List.of("Eve","Alice","Bob","Dave","Carol"));
        check(score, "task32", task32(), List.of(0L,1L,1L,2L,3L,5L,8L,13L,21L,34L));
        check(score, "task33", task33(), true);


        // --- слой 8: остаток эталона ---
        check(score, "task34", task34(), List.of(true, false, true));
        check(score, "task35", task35(EMPLOYEES), Map.of("Eng", "Eve", "Sales", "Dave"));
        check(score, "task36", task36(EMPLOYEES), "Eve");
        check(score, "task37", task37(EMPLOYEES), Map.of(
                new DeptActive("Eng", true), 2L, new DeptActive("Eng", false), 1L,
                new DeptActive("Sales", true), 1L, new DeptActive("Sales", false), 1L));
        check(score, "task39", task39(), List.of("ACC-1/EUR","ACC-1/USD","ACC-2/EUR","ACC-2/USD"));
        check(score, "task40", task40(ORDERS), bd(850));
        check(score, "task41", task41(), List.of("Eng:java","Eng:sql","Sales:excel"));
        check(score, "task42", task42(), List.of(1,2,4,8,16,32,64,128,256,512));
        check(score, "task43", task43(), List.of(List.of(1,2,3), List.of(10,4,5)));
        check(score, "task45", task45(TXNS), List.of(bd(100), bd(70), bd(120), bd(100)));

        System.out.printf("%n==== %d / %d ====%n", score[0], score[1]);
    }

    private static void check(int[] score, String name, Object actual, Object expected) {
        score[1]++;
        // BigDecimal.equals учитывает МАСШТАБ: 96000 и 96000.00 не равны.
        // Деньги сравниваем по значению — compareTo.
        boolean ok = (actual instanceof BigDecimal x && expected instanceof BigDecimal y)
                ? x.compareTo(y) == 0
                : Objects.equals(actual, expected);
        if (ok) score[0]++;
        System.out.printf("%s %-8s -> %s%s%n",
                ok ? "✅" : "❌",
                name,
                actual,
                ok ? "" : "   (ожидалось: " + expected + ")");
    }
}
