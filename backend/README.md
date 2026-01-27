# Backend - Demo Copilot Project

This is the backend of the demo application, built with **Java 21** and **Spring Boot 4.x**. The service handles business logic and integration with external APIs to provide duck data.

## 🚀 Technologies & Tools

- **Java 21**
- **Spring Boot 4.0.2**
- **Spring Data JPA**: For data persistence.
- **H2 Database**: In-memory database for development and testing.
- **RestClient**: Used for external API integrations (Spring Boot 3.2+).
- **Lombok**: To reduce boilerplate code.
- **JUnit 5 / Mockito**: For unit and integration testing.

## 🛠️ Configuration & Setup

### Prerequisites
- JDK 21 installed.
- Maven (optional, use the provided `./mvnw` wrapper).

### Main Commands

| Action | Command |
| :--- | :--- |
| **Build & Test** | `./mvnw clean install` |
| **Run Application** | `./mvnw spring-boot:run` |
| **Run Tests** | `./mvnw test` |

The application will be available at `http://localhost:8080`.

## 📡 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/duck` | Returns a random duck image through an external integration. |

### 🛠️ Database Access (H2)
You can access the H2 console to inspect the in-memory database while the application is running:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:duckdb`
- **Username**: `sa`
- **Password**: (empty)

## 🏗️ Architecture & Patterns

- **Constructor Injection**: Used in all components to facilitate testing and ensure immutability.
- **DTOs**: Java Records are used for data transfer objects (e.g., `DuckResponse`).
- **CORS**: Configured to allow requests from the frontend at `http://localhost:3000`.

## 🧪 Testing

The project follows rigorous testing standards:
- **Unit Tests**: Located in `src/test/java/.../service/` using `MockitoExtension`.
- **Integration Tests**: Located in `src/test/java/.../controller/` using `MockMvc` to validate API endpoints.
- **Mocking**: `@MockitoBean` is used to mock external dependencies in integration tests.

---
