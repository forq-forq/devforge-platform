# 🚀 DevForge Platform (MVP)

DevForge is an educational platform for learning programming with LMS features and code practice capabilities. Built using the **Modular Monolith** architecture approach to ensure scalability and maintainability.

## 🛠 Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 4.0.0 (Web, Security, Data JPA)
- **Database:** PostgreSQL (Production) / H2 (Development)
- **Frontend:** Thymeleaf (SSR) + Bootstrap 5
- **Build Tool:** Maven

## 📂 Project Structure

The project follows the **Package-by-Feature** structure:
- `user` - User management, authentication, and roles.
- `course` - Course creation, modules, and lessons catalog.
- `enrollment` - Student progress tracking.
- `practice` - (Planned) LeetCode-style code execution engine.

## 🚀 Getting Started

### Prerequisites
- JDK 21 installed
- Git installed

### Installation

1. Clone the repository:
```bash
   git clone https://github.com/forq-forq/DevForge-Platform.git
```
2. Navigate to the project directory:
```bash
    cd DevForge-Platform
```
3. Run the application (H2 database is configured by default):
```bash
    ./mvnw spring-boot:run
```
4. Open your browser:
    App: http://localhost:8080
    Database Console: http://localhost:8080/h2-console

## 📝 Roadmap

- [x] Project initialization & Architecture setup

- [x] User Module (Entity, Repository, Service)

- [ ] Spring Security Configuration

- [ ] Authentication UI (Login/Register)

- [ ] Course Module (CRUD)

- [ ] Student Enrollment Logic

- [ ] Docker Deployment

## 🤝 Contributing

This is an MVP project. Suggestions and pull requests are welcome!
