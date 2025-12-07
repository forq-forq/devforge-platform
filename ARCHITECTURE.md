# 🏗 Architecture Guidelines

DevForge follows the **Modular Monolith** architecture. This approach allows us to develop fast like a monolith but keeps boundaries clear for a potential microservices migration in the future.

## 1. Package Structure
We use **Package-by-Feature**, not Package-by-Layer.

```text
com.devforge.platform
├── user       // All user-related logic
├── course     // All course-related logic
├── security   // Global security config
└── common     // Shared utilities
```
## 2. Dependency Rules (The "Law of Demeter")

- **Module Isolation**: Modules (e.g., user and course) should interact only through public Service interfaces.
- **Repository Privacy:** Never inject a Repository from one module into a Service of another module.
    - ❌ `CourseService` -> `@Autowired UserRepository` (Forbidden)
    - ✅ `CourseService` -> `@Autowired UserService` (Allowed)
- **Circular Dependencies**: Avoid circular references between modules.

## 3. Coding Standards (SOLID)
- **Single Responsibility**:
    - Entities (`User`) represent the Database state.
    - DTOs (``RegisterRequest``) represent the API contract.
    - Do not expose Entities in Controllers.
- **Dependency Inversion:** Controllers depend on Service Interfaces (`UserService`), not implementations (`UserServiceImpl`).

## 4. Database
- **Development:** H2 (In-Memory).
- **Production:** PostgreSQL.
- **Migration**: Flyway/Liquibase (planned for later stages).