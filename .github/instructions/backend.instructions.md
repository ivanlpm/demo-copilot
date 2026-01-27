# Backend Development Instructions

Guidelines for Java 21 and Spring Boot 4.x development in this project.

## ☕ Core Java Patterns
- **Java Version**: Use Java 21 features (Records, Pattern Matching for switch/instanceof, Sequenced Collections).
- **Immutability**: Prefer `record` for DTOs and value objects to ensure thread safety and clarity.
- **Streams**: Use Stream API for collection manipulation, ensuring readability over complex loops.

## 🍃 Spring Boot & Architectural Patterns
- **Dependency Injection**: Always use **Constructor Injection**. Do not use `@Autowired` on fields.
- **Service Layer**: Business logic must reside in `@Service` classes, keeping `@Controller` thin.
- **REST Clients**: Use `RestClient` for external API integrations (replacing `RestTemplate`).
- **Persistence**: Use Spring Data JPA. Focus on derived query methods when possible.
- **Validation**: Use `@Valid` and Bean Validation annotations (`@NotNull`, `@NotBlank`, etc.) in Controller DTOs.

## 📡 API Design
- **REST Conventions**: 
  - Use plural nouns for resources (e.g., `/api/ducks`).
  - Use appropriate HTTP verbs: `GET` (read), `POST` (create), `PUT` (update), `PATCH` (partial update), `DELETE` (remove).
- **Error Handling**: Implement global exception handling using `@RestControllerAdvice` and `@ExceptionHandler`.
- **Response Structure**: Return consistent DTO structures. Use `ResponseEntity` to control status codes.

## 📝 Naming Conventions
- **Classes**: PascalCase (e.g., `DuckService`, `DuckResponse`).
- **Methods/Variables**: camelCase (e.g., `findAllDucks`, `duckName`).
- **Interfaces**: Do not prefix with `I`. Use descriptive implementation names (e.g., `DuckRepository` -> `DuckRepositoryImpl` if custom logic is needed).

## 🛠️ Performance & Security
- **Logging**: Use SLF4J (via `@Slf4j` or manual initialization). Avoid `System.out.println`.
- **Configuration**: Use `@ConfigurationProperties` for structured properties instead of multiple `@Value` annotations.
- **Database**: Ensure appropriate `@Transactional` boundaries, defaulting to `readOnly = true` for read operations.
