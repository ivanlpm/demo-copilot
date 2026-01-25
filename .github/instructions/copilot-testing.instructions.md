# Backend Instructions — **Java & Spring Boot**

**Purpose**  
Generate high-quality **backend code and tests** for a Java 21 / Spring Boot 3 project. Focus on meaningful assertions, deterministic tests, and clean architecture.

**Scope**  
Backend development guidance: **Domain logic, Services, Controllers, Repositories**, and their respective **Unit, Slice, and Integration** tests.

---

## Tech Stack
- **Java:** 21
- **Spring Boot:** 3.4+
- **Data:** Spring Data JPA (H2 for testing)
- **API:** REST (using `RestClient` for external calls)
- **Testing libs:** JUnit 5, AssertJ, Mockito, Spring Boot Test, MockMvc

---

## Global Backend Conventions
- **Naming:** `methodUnderTest_condition_expectedResult`.
- **Structure:** Given / When / Then blocks in tests.
- **DI:** Constructor injection only.
- **Patterns:** Use `@MockitoBean` for mocking Spring beans in integration tests.
- **REST Clients:** Use `RestClient` and test with `MockRestServiceServer`.

---

## Testing Examples

### Unit Testing (Service with Mockito)
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
  @Mock MyRepository repository;
  @InjectMocks MyService service;

  @Test
  void doSomething_validInput_returnsResult() {
    // given
    when(repository.findSomething()).thenReturn(Optional.of(data));
    // when
    var result = service.doSomething();
    // then
    assertThat(result).isEqualTo(expected);
  }
}
```

### Integration Testing (Controller with MockMvc)
```java
@SpringBootTest
class MyControllerTest {
    private MockMvc mockMvc;
    @Autowired private WebApplicationContext context;
    @MockitoBean private MyService myService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void getEndpoint_returnsOk() throws Exception {
        when(myService.getData()).thenReturn(mockData);
        mockMvc.perform(get("/api/data"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.field").value("value"));
    }
}
```

---

## Unit Testing (fast, isolated)

**What to test**
- Pure domain logic, transformers/mappers, utilities, and service methods with mocked collaborators

**Patterns**
- Mockito for behavior verification; **do not** start the Spring context
- Parameterized tests for boundary cases
- Property-based tests (optional) for numeric/format invariants

**Example (service + BigDecimal + fixed clock)**
```java
@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

  @Mock TaxService taxService;
  @InjectMocks PricingService service;

  private final Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);

  @Test
  @DisplayName("calculateTotal with two lines applies tax and HALF_UP rounding")
  void calculateTotal_withTwoLines_appliesTaxAndRounding() {
    // given
    var lines = List.of(new Line(new BigDecimal("12.345"), 2),
                        new Line(new BigDecimal("0.10"), 10));
    when(taxService.taxRate("DKK")).thenReturn(new BigDecimal("0.25"));

    // when
    var total = service.calculateTotal(lines, "DKK", clock);

    // then
    assertThat(total).isEqualByComparingTo(new BigDecimal("31.81"));
    verify(taxService).taxRate("DKK");
    verifyNoMoreInteractions(taxService);
  }
}
```

---

## Slice Testing (focused Spring slices)

### JPA Slice — `@DataJpaTest`
- Use H2 only for generic JPA behavior; for PG-specific features (jsonb/indexes) use **Testcontainers PostgreSQL** and Flyway migrations
- Validate constraints (unique/nullable/length/precision), cascades, and query correctness

**Example (PostgreSQL via Testcontainers + Flyway)**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryTest {
  @Container
  static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", pg::getJdbcUrl);
    r.add("spring.datasource.username", pg::getUsername);
    r.add("spring.datasource.password", pg::getPassword);
    r.add("spring.flyway.enabled", () -> true);
  }

  @Autowired OrderRepository repository;

  @Test
  void save_enforcesUniqueOrderNumber() {
    repository.save(new OrderEntity(113L, "A"));
    assertThatThrownBy(() -> repository.save(new OrderEntity(113L, "B")))
      .hasRootCauseInstanceOf(PersistenceException.class);
  }
}
```

---

## End-to-End (Integration) Testing

### GraphQL E2E (app context + DB + Testcontainers)
- Boot full Spring context via **`AbstractIntegrationTest`** with **Testcontainers (PostgreSQL + Kafka)**
- Use **`TestRestTemplate`** with `postGraphQL(query)` helper to execute queries against `/graphql` endpoint
- Store GraphQL queries in **separate `.graphql` files** under `src/test/resources/graphql/query/request/`
- Store expected responses in **`.json` files** under `src/test/resources/graphql/query/response/`
- Use **`JSONAssert`** with `commonUtil.assertResult()` for strict JSON comparison
- Mock repositories/services with **`@MockitoBean`** when needed
- Assert HTTP 200 and full response structure match

**Example (complete E2E test with mocked repository)**
```java
@ActiveProfiles("utest")
class OrderQueryIntegrationTest extends AbstractIntegrationTest {

  private static final String REQUEST_PATH = "graphql/query/request/";
  private static final String RESPONSE_PATH = "graphql/query/response/";

  @MockitoBean
  private OrderRepository orderRepository;

  @BeforeEach
  void setup() {
    initMocks();
  }

  @Test
  @DisplayName("given query with orderCode when query order then return full order details")
  void givenQueryWithOrderCode_whenQueryOrder_thenReturnOrder() throws IOException, JSONException {
    // given
    String query = buildQuery("PUR100");
    
    // when
    ResponseEntity<String> response = postGraphQL(query);
    
    // then
    commonUtil.assertResult(response, "order_result.json", RESPONSE_PATH);
  }

  private String buildQuery(String orderCode) throws IOException {
    return String.format(commonUtil.readString(REQUEST_PATH + "order.graphql"), "\"" + orderCode + "\"");
  }

  private void initMocks() {
    OrderEntity entity = new OrderEntity();
    entity.setOrderNumber(100L);
    entity.setOrderCode("PUR100");
    entity.setQuantity(500);
    when(orderRepository.findByOrderCode("PUR100")).thenReturn(Optional.of(entity));
  }
}
```

**GraphQL query file** (`src/test/resources/graphql/query/request/order.graphql`):
```graphql
query {
    order(orderCode: %s) {
        orderNumber
        orderCode
        orderState {
            orderStateNumber
            orderStateName
        }
        quantity
        isReadyForLabelProduction
    }
}
```

**Expected response file** (`src/test/resources/graphql/query/response/order_result.json`):
```json
{
  "data": {
    "order": {
      "orderNumber": 100,
      "orderCode": "PUR100",
      "orderState": {
        "orderStateNumber": 1,
        "orderStateName": "Open"
      },
      "quantity": 500,
      "isReadyForLabelProduction": false
    }
  }
}
```

**Notes:**
- `AbstractIntegrationTest` provides `postGraphQL(query)` which:
  - Sets `Authorization: Bearer {AUTH_TOKEN}` header automatically
  - Wraps query in JSON payload: `{"query": "..."}`
  - Posts to `/graphql` endpoint and returns `ResponseEntity<String>`
- Use `commonUtil.readString(path)` to load `.graphql` files from classpath
- Use `String.format()` to inject variables into queries (alternative: use `postGraphQLForString()` with variables ObjectNode)
- `commonUtil.assertResult()` asserts HTTP 200 and does strict JSONAssert comparison

### Kafka E2E (producer → consumer with Testcontainers)
- Use **Testcontainers Kafka** (managed by `AbstractIntegrationTest`)
- Publish messages using **`KafkaTemplate`** (available as `kafkaTemplateStringKey` or `kafkaTemplateLongKey`)
- Assert listener processes and persists effects using **Awaitility** for eventual consistency
- Verify **headers**: `X-Correlation-Id`, `X-Causation-Id`, `X-Producer` (when applicable)
- Load test data from JSON files using `commonUtil.getOptionalClassFromJson()`

**Example (listener flow with Avro payload)**
```java
@ActiveProfiles("utest")
class StyleEanIntegrationTest extends AbstractIntegrationTest {

  private final String PAYLOAD_PATH = "data/style/style_ean_PK5.json";

  @Autowired
  private StyleEanRepository styleEanRepository;

  @Test
  @DisplayName("given new style Avro DTO when StyleEanListener then save success")
  void givenNewStyleAvroDto_whenStyleEanListener_thenSaveSuccess() throws IOException {
    // given
    StyleEan styleEan = commonUtil.getOptionalClassFromJson(PAYLOAD_PATH, StyleEan.class).get();
    
    // when
    kafkaTemplateLongKey.send(KafkaTopics.STYLE_EAN, styleEan.getPublicId(), styleEan);
    
    // then
    await().atMost(Duration.ofMillis(AT_MOST_MILLIS)).untilAsserted(() -> {
      Optional<StyleEanEntity> optionalStyleEanEntity = styleEanRepository.findByStyleEanNumber(styleEan.getStyleEanNumber());
      assertTrue(optionalStyleEanEntity.isPresent());
      assertEquals(styleEan.getStyleEanNumber(), optionalStyleEanEntity.get().getStyleEanNumber());
    });
  }
}
```

**Notes:**
- `AbstractIntegrationTest` provides:
  - Testcontainers Kafka instance (automatically started and configured)
  - `kafkaTemplateStringKey` for messages with `String` keys
  - `kafkaTemplateLongKey` for messages with `Long` keys
  - `await()` method from Awaitility
  - `AT_MOST_MILLIS` constant for timeout
- Use **business keys** (publicId, orderNumber, etc.) as Kafka message keys for partitioning
- Load test payloads from JSON files in `src/test/resources/data/`
- Use Awaitility for async assertions with proper timeout

---

## Contract & Schema Tests
- **GraphQL SDL snapshot**: store canonical SDL (e.g., `src/test/resources/schema.graphqls.expected`) and compare generated schema to prevent drift
- **Messaging schema**: if Avro/JSON with versions, validate payload against schema; assert backward compatibility where required

---

## Resilience, Concurrency & Observability
- Use **Resilience4j** test hooks to assert retries/backoff and circuit-breaker states
- Model blocking flows under **virtual threads** in tests when relevant
- Expose/verify key **Micrometer timers** on critical services (optional)

---

## CI Guardrails
- `mvn -B clean install` must pass locally and in CI
- Static analysis (SpotBugs/Checkstyle/PMD) clean or justified suppressions
- No flakiness: isolate ports, use containers, fixed clock, seeded randomness

---

## Copilot Comment Prompts (paste in tests to steer generation)
```java
// UNIT: JUnit5 + AssertJ + Mockito for PricingService.calculateTotal.
// Use BigDecimal, HALF_UP(2), throw on qty < 0, and fixed Clock.

// SLICE-JPA: @DataJpaTest with Testcontainers PostgreSQL + Flyway.
// Verify unique constraint on OrderEntity(orderNumber) and FK integrity.

// E2E-GraphQL: Integration test extending AbstractIntegrationTest.
// Store query in .graphql file, mock repository with @MockitoBean, use postGraphQL() and assert with JSONAssert.
// Follow pattern: given-when-then with separate buildQuery() and initMocks() methods.

// E2E-KAFKA: Integration test extending AbstractIntegrationTest.
// Load JSON payload with commonUtil.getOptionalClassFromJson(), send with kafkaTemplateLongKey,
// assert persistence with Awaitility await().atMost().untilAsserted().
```

---

## Anti-Patterns (avoid)
- Field injection, static singletons, or hidden global state
- Business logic in resolvers/listeners/controllers
- Mocking JPA for repository tests (use slice/containers instead)
- Swallowing exceptions or asserting only on messages
- Time-dependent tests without a **fixed** `Clock`
- N+1 queries (unbatched DataLoader / improper JPA fetching)

---

## Definition of Done (tests)
- Meaningful unit, slice, and/or E2E coverage for new logic
- Deterministic, isolated, and repeatable tests (no flakes)
- Observability where relevant; no new static-analysis violations
- GraphQL schema/contracts guarded by snapshots or validators
- Kafka flows validated end-to-end with keys and headers asserted
