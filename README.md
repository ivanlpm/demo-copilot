# Demo Copilot Project

A full-stack demonstration application featuring a Java Spring Boot backend and a Next.js frontend. This project showcases modern development patterns, automated testing, and CI/CD integration.

## 🏗️ Project Structure

The repository is organized into two main components:

- **[backend/](backend/)**: Spring Boot 4 application providing a REST API and external integrations.
- **[frontend/](frontend/)**: Next.js 16 application with React 19 and Tailwind CSS 4.

## 🚀 Tech Stack

### Backend
- **Java 21** & **Spring Boot 4.x**
- **Spring Data JPA** with **H2** (In-memory database)
- **RestClient** for external service communication
- **JUnit 5**, **Mockito**, **AssertJ** for testing
- **Maven** for build management

### Frontend
- **Next.js 16** (App Router) & **React 19**
- **TypeScript**
- **Tailwind CSS 4** for styling
- **Lucide React** for icons

## 🛠️ Getting Started

### Prerequisites
- [JDK 21+](https://adoptium.net/)
- [Node.js 20+](https://nodejs.org/)
- [npm](https://www.npmjs.com/) or similar package manager

### Local Development

#### 1. Start the Backend
```bash
cd backend
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`. You can also access the H2 console at `http://localhost:8080/h2-console`.

#### 2. Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
The web application will be available at `http://localhost:3000`.

## 🧪 Testing & Quality

### Backend
- Run tests: `mvn clean verify` (or `./mvnw clean verify`)
- Tests include unit tests for services and integration tests for controllers.

### Frontend
- Linting: `npm run lint`
- Build: `npm run build`

## 🔗 CI/CD
The project uses GitHub Actions for continuous integration. The workflow is defined in [.github/workflows/ci.yml](.github/workflows/ci.yml) and runs on every push and pull request to the `main` branch, ensuring both backend and frontend build and pass tests successfully.

## 📄 License
Check individual folders for specific documentation and licensing details.
