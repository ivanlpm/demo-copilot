# Copilot Instructions - Demo Copilot Project

Comprehensive guide for AI agents working on the Java/Spring Boot backend and Next.js frontend.

## 🏗️ Architecture & Tech Stack

### Backend (`backend/`)
- **Core**: Java 21, Spring Boot 4.x
- **Persistence**: Spring Data JPA with H2 (in-memory)
- **API Clients**: `RestClient` (Spring Boot 3.2+) for external integrations
- **Testing**: JUnit 5, AssertJ, Mockito, MockMvc, `@MockitoBean` for integration mocks

### Frontend (`frontend/`)
- **Core**: Next.js 16 (App Router), React 19, TypeScript
- **Styling**: Tailwind CSS 4

## 🛠️ Developer Workflows

- **Backend**: 
  - Build/Test: `mvn clean install`
  - Run: `mvn spring-boot:run`
- **Frontend**:
  - Dev: `npm run dev`

## 📌 Project Conventions & Patterns

### ☕ Backend Patterns
- **Constructor Injection**: Always use constructor injection for dependencies.
- **Testing Structure**: Follow `Given / When / Then` blocks. Use `methodUnderTest_condition_expectedResult` naming.
- **External APIs**: Use `RestClient` and test with `MockRestServiceServer`.
- **Wait/Polling**: Avoid explicit waits; use `Awaitility` if needed (though not currently in `pom.xml`).
- **Data**: Prefer immutable Records for DTOs (e.g., `DuckResponse.java`).

### ⚛️ Frontend Patterns
- **Hooks**: Logic for fetching/managing state should be in custom hooks (e.g., [hooks/useDuck.ts](frontend/hooks/useDuck.ts)).
- **Component UI**: Shared UI components live in [components/ui/](frontend/components/ui/).
- **Tailwind**: Use Tailwind 4 utility classes directly in components.

## 🔌 Integration Points
- **CORS**: Backend [DuckController.java](backend/src/main/java/com/example/demo/controller/DuckController.java) allows `http://localhost:3000`.
- **Base URLs**: Frontend defaults to `http://localhost:8080/api/duck`. Backend `apiUrl` for the Duck API is configured in `application.properties`.

## ✅ Quality Standards
- **Testing**: Every backend service method needs a Unit Test (`MockitoExtension`). Every backend Controller needs an Integration Test (`MockMvc`).

## 📚 Instruction Guides
For detailed guidelines on specific areas of the project, refer to the following instruction files:
- **Backend**: [.github/instructions/backend.instructions.md](.github/instructions/backend.instructions.md)
- **Backend Testing**: [.github/instructions/backend-testing.instructions.md](.github/instructions/backend-testing.instructions.md)
- **Frontend**: [.github/instructions/frontend.instructions.md](.github/instructions/frontend.instructions.md)
- **Code Quality**: [.github/instructions/code-quality.instructions.md](.github/instructions/code-quality.instructions.md)
