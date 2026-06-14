/**
 * Маппинг GoF + Concurrency паттернов → Spring-механизмы.
 *
 * На senior-собесе вопрос звучит так:
 *   "Где вы видели [паттерн] в реальном коде / в Spring?"
 * Ниже — готовые ответы с конкретными классами и аннотациями.
 *
 * Запускать не нужно — это справочник для чтения.
 */
public class SpringMapping {

    // ─────────────────────────────────────────────────────────────
    // CREATIONAL
    // ─────────────────────────────────────────────────────────────

    // SINGLETON
    //   Каждый Spring-бин по умолчанию — Singleton (один экземпляр на ApplicationContext).
    //   @Service, @Repository, @Component, @Bean — все создаются ровно один раз.
    //   Явный scope: @Scope("singleton") — дефолт, писать не нужно.
    //   Аналог Holder-паттерна: Spring сам управляет ленивой инициализацией через @Lazy.
    //   Enum-синглтон → нет прямого аналога, но @Bean(name) гарантирует единственность.

    // FACTORY METHOD
    //   @Bean-метод в @Configuration — это фабричный метод:
    //     @Bean PaymentProcessor cardProcessor() { return new CardProcessor(); }
    //   FactoryBean<T> — классический Spring factory для сложной инициализации (MyBatis MapperFactoryBean).
    //   BeanFactory сам по себе — реализация паттерна Factory.

    // ABSTRACT FACTORY
    //   @Configuration + @Profile("prod") / @Profile("test") — разные фабрики на разных окружениях:
    //     @Profile("prod")  @Bean PspFactory stripePspFactory() { ... }
    //     @Profile("test")  @Bean PspFactory mockPspFactory()   { ... }
    //   Весь @Configuration-класс — это AbstractFactory: гарантирует когерентный набор бинов.

    // BUILDER
    //   UriComponentsBuilder, MockMvcRequestBuilders, WebClient.builder(),
    //   RestClient.builder(), HttpHeaders builder, SecurityFilterChain builder (HttpSecurity).
    //   @Builder от Lombok генерирует тот же паттерн для domain-классов.
    //   TestRestTemplate / MockMvc — builder API для тестов.

    // PROTOTYPE
    //   @Scope("prototype") — новый экземпляр на каждый вызов getBean().
    //   @Scope("request")   — новый экземпляр на каждый HTTP-запрос.
    //   @Scope("session")   — новый экземпляр на каждую HTTP-сессию.
    //   ObjectFactory<T> / ObjectProvider<T> — ленивое создание prototype-бина внутри singleton.

    // ─────────────────────────────────────────────────────────────
    // STRUCTURAL
    // ─────────────────────────────────────────────────────────────

    // ADAPTER
    //   HandlerAdapter в Spring MVC — адаптирует любой @Controller к единому интерфейсу DispatcherServlet.
    //   JpaVendorAdapter — адаптирует Hibernate/EclipseLink к Spring Data JPA.
    //   MessageConverter (HttpMessageConverter) — адаптирует Java-объекты к HTTP body и обратно.
    //   @EnableWebMvc регистрирует набор адаптеров по умолчанию.

    // BRIDGE
    //   Spring Data Repository + несколько реализаций:
    //     PaymentRepository (абстракция) → JpaPaymentRepository / MongoPaymentRepository (реализация).
    //   PlatformTransactionManager — абстракция над JdbcTransactionManager / JpaTransactionManager / KafkaTransactionManager.
    //   ResourceLoader — абстракция над ClassPath / FileSystem / URL ресурсами.

    // COMPOSITE
    //   CompositeValidator (org.springframework.validation) — список Validator, вызываемых как один.
    //   CompositeMeterRegistry (Micrometer) — метрики пишутся в несколько registry одновременно.
    //   AuthenticationManager → ProviderManager содержит список AuthenticationProvider (Composite).
    //   HandlerExceptionResolverComposite — цепочка resolvers как единый resolver.

    // DECORATOR
    //   @Transactional    — CGLIB/JDK-прокси оборачивает бин, добавляет управление транзакцией.
    //   @Cacheable        — прокси добавляет кэширование поверх метода.
    //   @PreAuthorize     — прокси добавляет security-проверку перед вызовом.
    //   @Async            — прокси переносит вызов в отдельный поток.
    //   @Retryable        — прокси добавляет retry-логику (Spring Retry).
    //   Каждая из этих аннотаций — отдельный Decorator через Spring AOP.
    //   Порядок: @Transactional → @Cacheable → @PreAuthorize управляется через @Order.

    // FACADE
    //   JdbcTemplate — фасад над JDBC: управляет Connection, PreparedStatement, ResultSet, exception mapping.
    //   RestTemplate / RestClient — фасад над HTTP: маршалинг, коды ответов, error handling.
    //   ApplicationContext сам — фасад над всей конфигурацией Spring.
    //   Spring Security — фасад над аутентификацией, авторизацией, CSRF, CORS.

    // FLYWEIGHT
    //   Spring-бины scope=singleton — один экземпляр на весь контекст, разделяется всеми потребителями.
    //   StringValueResolver, ConversionService — один экземпляр, используется повсеместно.
    //   Hibernate 2nd-level cache + @SharedCacheMode — кэшированные entity как flyweight.

    // PROXY
    //   Spring AOP — каждый бин с @Transactional/@Cacheable обёрнут в JDK Proxy или CGLIB Proxy.
    //   @Lazy на параметре конструктора — ProxyFactoryBean создаёт ленивый прокси.
    //   @FeignClient, @RestClient — интерфейс без реализации; Spring генерирует Proxy в рантайме.
    //   Spring Data Repository — интерфейс без реализации; JDK Proxy с SimpleJpaRepository внутри.
    //   Разница JDK Proxy vs CGLIB: JDK требует интерфейс, CGLIB наследует класс (поэтому final-классы не работают с AOP).

    // ─────────────────────────────────────────────────────────────
    // BEHAVIORAL
    // ─────────────────────────────────────────────────────────────

    // CHAIN OF RESPONSIBILITY
    //   SecurityFilterChain — цепочка фильтров, каждый может прервать или пропустить запрос дальше.
    //   OncePerRequestFilter — базовый класс для звена в цепочке.
    //   HandlerInterceptor (preHandle / postHandle / afterCompletion) — цепочка в Spring MVC.
    //   ExceptionHandlerExceptionResolver → DefaultHandlerExceptionResolver → ... — цепочка обработки ошибок.
    //   Spring Batch: Step → Step → Step — цепочка шагов с возможностью прерывания.

    // COMMAND
    //   @Async + CompletableFuture — команда отправляется в очередь и выполняется асинхронно.
    //   Spring Batch Job — команда запускается, результат отслеживается через JobExecution.
    //   ApplicationEvent + ApplicationEventPublisher — команда публикуется, обрабатывается @EventListener.
    //   @Scheduled — команда ставится в расписание.
    //   CommandLineRunner / ApplicationRunner — команды при старте приложения.

    // INTERPRETER
    //   Spring Expression Language (SpEL): @PreAuthorize("hasRole('ADMIN') and #id == principal.id").
    //   @Query("SELECT u FROM User u WHERE u.age > :age") — JPQL интерпретируется Hibernate.
    //   @Value("${server.port:8080}") — интерпретация placeholder-выражений.
    //   Thymeleaf / FreeMarker шаблонизаторы — интерпретируют выражения в шаблонах.

    // ITERATOR
    //   Spring Data: Page<T>, Slice<T>, ScrollPosition — ленивая постраничная итерация по БД.
    //   ResultSet → RowMapper — итерация по строкам без exposing курсора.
    //   JdbcTemplate.query() — скрывает Iterator над ResultSet.
    //   Stream<T> из Spring Data — ленивый курсор по БД через Iterator.

    // MEDIATOR
    //   ApplicationContext — бины не знают друг о друге, общаются через контейнер.
    //   ApplicationEventPublisher / @EventListener — издатель не знает подписчиков.
    //   @FeignClient — сервисы общаются через декларативный интерфейс, не напрямую.
    //   Spring Cloud Gateway — медиатор между клиентами и микросервисами.

    // MEMENTO
    //   @Transactional — savepoint/rollback сохраняет и восстанавливает состояние транзакции.
    //   Spring Session — снимок HTTP-сессии, может восстанавливаться из Redis.
    //   Hibernate dirty-checking — снимок состояния entity при загрузке, сравнивается при flush.
    //   EntityManager.detach() / merge() — явное управление снимками состояния.

    // OBSERVER
    //   ApplicationEventPublisher.publishEvent() + @EventListener — канонический Observer в Spring.
    //   @TransactionalEventListener — слушатель вызывается после коммита транзакции.
    //   Spring Actuator + Micrometer — метрики публикуются в наблюдателей (Prometheus, Grafana).
    //   @NotifyChange (Spring Shell) — реактивное обновление UI при изменении модели.
    //   В Spring Boot: ApplicationListener<ContextRefreshedEvent> — слушатель старта контекста.

    // STATE
    //   Spring Batch: JobStatus (STARTING → STARTED → COMPLETED / FAILED) — state machine статуса job.
    //   Spring Statemachine — явная библиотека для State-паттерна: состояния, переходы, action.
    //   Spring Security: SecurityContext хранит состояние аутентификации запроса.
    //   Lifecycle бина: SINGLETON_LIFECYCLE — instantiate → populate → init → use → destroy.

    // STRATEGY
    //   Spring DI инжектирует List<T> — все реализации интерфейса в список:
    //     @Autowired List<FeeStrategy> strategies; // все бины типа FeeStrategy
    //   @Qualifier("cardFee") FeeStrategy fee — выбор конкретной стратегии по имени.
    //   Comparator.comparing() / sorted() — стратегия сортировки передаётся как параметр.
    //   ResourceHandlerRegistry — стратегии обработки статических ресурсов.
    //   AuthenticationStrategy в Spring Security — стратегия выбора метода аутентификации.

    // TEMPLATE METHOD
    //   JdbcTemplate.query() — скелет: получить Connection → PreparedStatement → execute → map → close.
    //     Переопределяем только RowMapper (шаг "map").
    //   AbstractMessageListenerContainer — скелет чтения из очереди; переопределяем onMessage().
    //   WebSecurityConfigurerAdapter (устар.) / SecurityFilterChain — скелет конфигурации security.
    //   AbstractHealthIndicator — переопределяем только doHealthCheck().
    //   Spring Batch AbstractItemReader — скелет чтения; переопределяем doRead().

    // VISITOR
    //   BeanDefinitionVisitor — обходит все BeanDefinition в контексте.
    //   ExpressionVisitor в SpEL — обходит AST выражения.
    //   Jackson ObjectMapper — Visitor по дереву JSON-нодов (JsonNode.traverse()).
    //   Spring MVC HandlerMethodArgumentResolver — visitor, который "посещает" параметры метода.

    // ─────────────────────────────────────────────────────────────
    // CONCURRENT
    // ─────────────────────────────────────────────────────────────

    // THREAD POOL
    //   @Async + ThreadPoolTaskExecutor — аннотация переносит метод в пул потоков.
    //   @EnableAsync активирует обработку @Async через AOP-прокси.
    //   Настройка: ThreadPoolTaskExecutor.setCorePoolSize / setMaxPoolSize / setQueueCapacity.
    //   Spring Boot auto-configures: spring.task.execution.pool.core-size=8.

    // PRODUCER-CONSUMER
    //   @RabbitListener / @KafkaListener — consumer читает из очереди, publisher пишет.
    //   BlockingQueue внутри ThreadPoolExecutor — встроенный Producer-Consumer в пуле потоков.
    //   Spring Integration Channel — типизированный канал между producer и consumer.
    //   spring.kafka.consumer.max-poll-records — управление backpressure на consumer.

    // COMPLETABLEFUTURE / SCATTER-GATHER
    //   @Async возвращает CompletableFuture<T> — вызывающий не блокируется.
    //   WebClient (WebFlux) — Mono.zip() / Flux.merge() для параллельных вызовов.
    //   Spring Cloud OpenFeign с @Async — параллельные вызовы к нескольким сервисам.
    //   RestClient + CompletableFuture.allOf() — scatter-gather на уровне сервисного слоя.

    // CIRCUIT BREAKER
    //   Resilience4j @CircuitBreaker — аннотация с fallbackMethod, автоматический OPEN/HALF_OPEN.
    //   Spring Cloud Circuit Breaker — абстракция над Resilience4j / Hystrix / Sentinel.
    //   @Bulkhead (Resilience4j) — изоляция пулов потоков, предотвращает cascade failure.
    //   spring.cloud.circuitbreaker.resilience4j.enabled=true.

    // RETRY
    //   @Retryable(maxAttempts=3, backoff=@Backoff(delay=100, multiplier=2)) — Spring Retry.
    //   @Recover — метод-fallback после исчерпания попыток.
    //   RetryTemplate — программный аналог с ExponentialBackOffPolicy + jitter.
    //   Spring Kafka: spring.kafka.consumer.retry-topic-suffix — retry topics для async retry.

    // READ-WRITE LOCK
    //   @Cacheable + @CachePut + @CacheEvict — разделение read/write на уровне кэша.
    //   Spring Data Redis: Lettuce поддерживает RedisRWLock через SETNX / Lua scripts.
    //   Hibernate optimistic locking (@Version) — read без блокировки, write с version-check.
    //   StampedLock как альтернатива в низкоуровневых Spring-компонентах (optimistic read).

    public static void main(String[] args) {
        System.out.println("SpringMapping — справочник, не для запуска.");
        System.out.println("Читай исходник: src/SpringMapping.java");
    }
}
