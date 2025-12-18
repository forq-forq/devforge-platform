
# DevForge Platform 🛠️🎓

**DevForge** is an educational platform designed to teach Algorithms and Data Structures through interactive practice, quizzes, and AI-powered mentorship. 

Website: https://devforge-platform.onrender.com/
GitHub: https://github.com/forq-forq/devforge-platform

> **Course Project:** Design Patterns and Architecture  
> **Authors:** Igonin Nikita, Kustaubay Madiyar, 
> **Status:** MVP / Prototype

---

## 🏗️ Architecture Overview

The project follows a **Monolithic Layered Architecture** built with Spring Boot. It emphasizes separation of concerns and uses strict strict typing.

### High-Level Structure
1.  **Presentation Layer (`web`)**: Handles HTTP requests, Thymeleaf templates, and DTO mapping.
2.  **Business Layer (`service`)**: Contains core business logic, transactions, and integrations.
3.  **Persistence Layer (`repository`)**: Data access using Spring Data JPA.
4.  **Domain Layer (`domain`)**: Database entities representing the business model.

### 🏛️ Design Patterns Used

This project demonstrates the practical application of several GoF patterns:

| Pattern | Where it is used | Purpose |
| :--- | :--- | :--- |
| **Strategy** | `GamificationService` | Calculates XP based on `LessonType` (Lecture, Quiz, Practice) without `switch-case`. |
| **Builder** | All Entities & DTOs | Simplifies object creation (via Lombok `@Builder`). |
| **Facade** | `GeminiService` | Hides the complexity of external REST calls to Google Gemini AI API. |
| **Repository** | `UserRepository`, `CourseRepository` | Abstracts data access logic. |
| **Singleton** | Service Beans | Default Spring scope for stateless services. |
| **DTO** | `CreateCourseRequest`, etc. | Decouples internal database structure from the API contract. |

---

## ✨ Key Features

*   **👨‍🏫 Course Management:** Teachers can create Drafts, add Lessons (Video, Coding, Quiz), and Publish courses.
*   **💻 Interactive Code Runner:** In-memory Java compiler allows students to run code and get immediate feedback.
*   **🤖 AI Mentor (Gemini):**
    *   Summarize lectures.
    *   Explain coding errors.
    *   Perform code reviews.
*   **🎮 Gamification:** XP system, Ranks (Novice -> Grandmaster), and Leaderboards.
*   **📜 Certification:** Auto-generated certificates upon course completion.
*   **🔐 Security:** Role-Based Access Control (RBAC) for Students and Teachers.

---

## 🛠️ Tech Stack

*   **Language:** Java 21 (LTS)
*   **Framework:** Spring Boot 3.2 (Web, Security, Data JPA)
*   **Database:** PostgreSQL 15
*   **Frontend:** Thymeleaf + Bootstrap 5 + Monaco Editor
*   **AI:** Google Gemini Pro API
*   **Infrastructure:** Docker & Docker Compose (deployed on Render)

---

## 🚀 Getting Started

### Prerequisites
*   Docker & Docker Compose
*   Java 21 SDK (optional, if running locally without Docker)
*   Maven

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-repo/devforge.git
    cd devforge
    ```

2.  **Configure Environment:**
    Create a `.env` file or export the variable (or use the default in docker-compose):
    ```bash
    export GEMINI_API_KEY=your_api_key_here
    ```

3.  **Run with Docker:**
    ```bash
    docker-compose up --build
    ```
    The app will be available at `http://localhost:8080`.

4.  **Demo Credentials:**
    *   **Teacher:** `teacher@devforge.com` / `password`
    *   **Student:** `student@devforge.com` / `password`

---

## 📂 Project Structure Map

```text
src/main/java/com/devforge/platform
├── common          # Shared utilities & AI Service
├── course          # Course management (Course, Lesson)
├── enrollment      # Student progress tracking & Certificates
├── practice        # Code execution engine & Problems
├── quiz            # Quiz logic & Grading
├── review          # Course reviews & ratings
├── security        # Spring Security config
└── user            # User management, Auth, Gamification
```

---

## ⚠️ Known Limitations (MVP)

-   **Code Sandbox:** Currently, user code runs within the JVM. For production, this must be isolated in a container.
    
-   **Scalability:** Lesson content uses a polymorphic-like single table. Splitting into joined inheritance would be better for scale.

## Future Improvements:
- **Frontend:** In the future, we want to rewrite the frontend using the more modern, de facto standard React framework.

- **Architecture change**: As functionality improves, it will be logical to move to a microservice architecture.
