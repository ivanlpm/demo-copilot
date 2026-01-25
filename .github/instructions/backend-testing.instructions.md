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
- **Naming:** `methodUnderTest_condition_expectedResult`. Use `@DisplayName` for clarity.
- **Structure:** Given / When / Then blocks in tests.
- **DI:** Constructor injection only.
- **Patterns:** Use `@MockitoBean` for mocking Spring beans in integration tests.
- **REST Clients:** Use `RestClient` and test with `MockRestServiceServer`.
- **Formatting:** Use standard Java formatting.

---

## Testing Examples

### Unit Testing (Service with Mockito)
Use `MockitoExtension` for fast, isolated tests of business logic.

```java
@ExtendWith(MockitoExtension.class)
class DuckServiceTest {
    @Mock private DuckRepository duckRepository;
    private DuckService duckService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        duckService = new DuckService(builder, duckRepository, "https://api.test.com");
    }

    @Test
    @DisplayName("getRandomDuck should fetch from API and save to repository")
    void getRandomDuck_Success_SavesToRepo() {
        // given
        String jsonResponse = "{\"url\": \"https://duck.com/img.jpg\", \"message\": \"Quack!\"}";
        mockServer.expect(requestTo("https://api.test.com/random"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when
        DuckResponse response = duckService.getRandomDuck();

        // then
        assertThat(response).isNotNull();
        assertThat(response.url()).isEqualTo("https://duck.com/img.jpg");
        verify(duckRepository).save(any());
        mockServer.verify();
    }
}
```

### Integration Testing (Controller with MockMvc)
Use `@SpringBootTest` to test endpoints and their interaction with the context.

```java
@SpringBootTest
class DuckControllerTest {
    private MockMvc mockMvc;
    @Autowired private WebApplicationContext context;
    @MockitoBean private DuckService duckService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    @DisplayName("GET /api/duck should return duck data")
    void getDuck_ReturnsDuckData() throws Exception {
        when(duckService.getRandomDuck()).thenReturn(new DuckResponse("url", "msg"));
        
        mockMvc.perform(get("/api/duck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("url"));
    }
}
```

### Repository Testing (JPA)
Use `@SpringBootTest` and `@Transactional` for testing repository methods with H2.

```java
@SpringBootTest
@Transactional
class DuckRepositoryTest {
    @Autowired private DuckRepository repository;

    @Test
    void shouldSaveAndFindDuck() {
        Duck duck = new Duck("url", "msg");
        Duck saved = repository.save(duck);
        assertThat(repository.findById(saved.getId())).isPresent();
    }
}
```

---

## CI & Quality
- All tests must pass with `./mvnw test`.
- No field injection.
- Keep tests deterministic.
