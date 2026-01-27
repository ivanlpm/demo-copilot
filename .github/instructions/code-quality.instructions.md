# Code Quality Standards

Global standards to maintain high-quality code across both backend and frontend.

## 💎 Clean Code Principles
- **Descriptive Naming**: Variables, functions, and classes should describe their purpose without needing comments.
- **Small Functions**: Functions should do one thing and do it well (Single Responsibility Principle).
- **No Side Effects**: Prefer pure functions where possible to improve predictability and testability.
- **KISS**: Keep it Simple, Stupid. Avoid over-engineering or premature optimization.
- **DRY**: Don't Repeat Yourself. Extract shared logic into reusable utilities or components.

## 📐 SOLID Principles
- **S**: Single Responsibility – A class/function should have only one reason to change.
- **O**: Open/Closed – Software entities should be open for extension but closed for modification.
- **L**: Liskov Substitution – Subtypes must be substitutable for their base types.
- **I**: Interface Segregation – Clients should not be forced to depend on methods they do not use.
- **D**: Dependency Inversion – Depend on abstractions, not concretions.

## 📝 Documentation & Comments
- **Javadoc/TSDoc**: Provide descriptive documentation for all public classes, methods, and interfaces.
- **Avoid Commenting the Obvious**: Comments should explain *why* something is done, not *what* is being done (the code should explain the "what").
- **No Dead Code**: Do not leave commented-out code in the repository. Remove it and rely on version control (Git).

## 🛡️ Error Handling
- **Defensive Programming**: Validate inputs early and often.
- **Descriptive Errors**: Fail gracefully with messages that help developers or users understand what went wrong.
- **Logging**: Log errors with enough context (stack traces, relevant variables) to facilitate debugging.

## 🧪 Testing Mindset
- **High Coverage**: Aim for high test coverage on critical business logic.
- **Isolated Tests**: Unit tests should be fast and independent of external systems (use Mocks).
- **Assertive Names**: Test names should clearly state the scenario and expected outcome.

## 🧹 Code Hygiene
- **Consistent Formatting**: Follow the established linting/formatting rules (ESLint for frontend, standard Java styles for backend).
- **Small Commits**: Make frequent, small, and logical commits with clear descriptions.
- **Environment Variables**: Never hardcode secrets or configuration values. Use `.env` or `application.properties`.
