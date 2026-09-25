# Architecture Document

# Online Quiz Application

## 1. Architecture Overview

The application uses a client-server architecture.

```text
React Frontend
      |
      | HTTP/REST
      v
Spring Boot Backend
      |
      +----------------------+
      |                      |
      v                      v
PostgreSQL Database      AI Quiz Service
                              |
                              v
                         AI Provider
```

The backend remains the source of truth for authentication, authorization, scoring, validation, persistence, and business rules.

---

# 2. Technology Stack

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- Bean Validation
- JWT
- Maven

## Database

- PostgreSQL

## Frontend

- React
- JavaScript or TypeScript
- HTML
- CSS

## AI

- External AI provider through a dedicated provider abstraction.
- Structured JSON output.
- Backend validation before persistence.

The exact AI provider should be configurable rather than tightly coupling business logic to one provider.

## Development Tools

- IntelliJ IDEA / VS Code
- Git
- GitHub
- Postman
- PostgreSQL
- pgAdmin

---

# 3. Backend Architecture

Use layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Supporting layers:

- Security
- DTO
- Entity
- Exception
- Mapper
- Configuration
- AI

---

# 4. Backend Folder Structure

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/quizapp/
│   │   │       ├── QuizApplication.java
│   │       │
│   │       ├── config/
│   │       │   └── ...
│   │       │
│   │       ├── controller/
│   │       │   ├── AuthController.java
│   │       │   ├── UserController.java
│   │       │   ├── QuizController.java
│   │       │   ├── QuestionController.java
│   │       │   ├── AttemptController.java
│   │       │   └── AIQuizController.java
│   │       │
│   │       ├── service/
│   │       │   ├── AuthService.java
│   │       │   ├── UserService.java
│   │       │   ├── QuizService.java
│   │       │   ├── QuestionService.java
│   │       │   ├── AttemptService.java
│   │       │   └── AIQuizService.java
│   │       │
│   │       ├── repository/
│   │       │   ├── UserRepository.java
│   │       │   ├── QuizRepository.java
│   │       │   ├── QuestionRepository.java
│   │       │   ├── OptionRepository.java
│   │       │   ├── AttemptRepository.java
│   │       │   └── QuizGenerationRequestRepository.java
│   │       │
│   │       ├── entity/
│   │       │   ├── User.java
│   │       │   ├── Quiz.java
│   │       │   ├── Question.java
│   │       │   ├── Option.java
│   │       │   ├── QuizAttempt.java
│   │       │   └── QuizGenerationRequest.java
│   │       │
│   │       ├── dto/
│   │       │   ├── auth/
│   │       │   ├── quiz/
│   │       │   ├── question/
│   │       │   ├── attempt/
│   │       │   └── ai/
│   │       │
│   │       ├── security/
│   │       │   ├── JwtService.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── SecurityConfig.java
│   │       │
│   │       ├── ai/
│   │       │   ├── AIQuizProvider.java
│   │       │   ├── AIQuizResponseValidator.java
│   │       │   └── provider/
│   │       │       └── ...
│   │       │
│   │       ├── exception/
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── ResourceNotFoundException.java
│   │       │   ├── BadRequestException.java
│   │       │   ├── UnauthorizedException.java
│   │       │   └── ForbiddenException.java
│   │       │
│   │       └── enums/
│   │           ├── Role.java
│   │           ├── Difficulty.java
│   │           ├── QuizSource.java
│   │           └── QuizStatus.java
│   │
│   └── resources/
│       ├── application.properties
│       └── db/
│           └── migration/
│
├── src/test/
├── pom.xml
└── README.md
```

---

# 5. Frontend Structure

```text
frontend/
├── src/
│   ├── components/
│   ├── pages/
│   │   ├── Login/
│   │   ├── Register/
│   │   ├── Dashboard/
│   │   ├── Quiz/
│   │   ├── Results/
│   │   ├── History/
│   │   ├── Profile/
│   │   └── Admin/
│   │       ├── Dashboard/
│   │       ├── Quizzes/
│   │       ├── Questions/
│   │       └── AIQuizGenerator/
│   │
│   ├── services/
│   │   └── api.js
│   │
│   ├── context/
│   │   └── AuthContext.jsx
│   │
│   ├── hooks/
│   ├── utils/
│   ├── styles/
│   ├── App.jsx
│   └── main.jsx
│
├── package.json
└── README.md
```

---

# 6. Database Entities

## User

- id
- name
- email
- password
- role
- createdAt

## Quiz

- id
- title
- description
- category
- difficulty
- source
- status
- createdBy
- createdAt
- publishedAt

## Question

- id
- quizId
- questionText
- questionOrder
- explanation
- difficulty

## Option

- id
- questionId
- optionText
- isCorrect

## QuizAttempt

- id
- userId
- quizId
- score
- totalQuestions
- correctAnswers
- incorrectAnswers
- percentage
- startedAt
- completedAt

## QuizGenerationRequest

- id
- requestedBy
- topic
- category
- difficulty
- questionCount
- generationReason
- status
- createdAt

---

# 7. AI Architecture

The AI integration must be isolated from core business logic.

```text
AIQuizController
       ↓
AIQuizService
       ↓
AIQuizProvider
       ↓
External AI Provider
       ↓
Structured Generated Quiz
       ↓
AIQuizResponseValidator
       ↓
QuizService
       ↓
Repository
       ↓
Database
```

The provider abstraction allows another AI provider or a mock provider to be used later.

Example:

```java
public interface AIQuizProvider {

    GeneratedQuiz generateQuiz(
        QuizGenerationRequest request
    );
}
```

The AI provider must not directly access repositories.

---

# 8. AI Generation Flow

```text
Admin/User Request
       ↓
Validate Request
       ↓
AIQuizService
       ↓
AI Provider
       ↓
Structured JSON
       ↓
Parse Response
       ↓
Validate Quiz
       ↓
Business Validation
       ↓
Save as DRAFT/PENDING_REVIEW
       ↓
Admin Approval OR Auto-Publish
```

---

# 9. Personalized Quiz Flow

```text
User completes quiz
        ↓
Attempt stored
        ↓
Backend calculates performance
        ↓
Performance summary created
        ↓
AIQuizService
        ↓
AI Provider
        ↓
Personalized quiz
        ↓
Validation
        ↓
Save
        ↓
Publish according to configured policy
```

Only controlled performance information should be sent to the AI.

---

# 10. Authentication Flow

```text
User
 ↓
Login
 ↓
Spring Security
 ↓
Credential validation
 ↓
JWT generated
 ↓
Frontend
 ↓
Protected API request
 ↓
JWT Filter
 ↓
Authentication
 ↓
Authorization
 ↓
Controller
```

---

# 11. Quiz Flow

```text
User
 ↓
Login
 ↓
Quiz Dashboard
 ↓
Select Quiz
 ↓
Start Attempt
 ↓
Load Questions
 ↓
Answer Question
 ↓
Submit Answer
 ↓
Backend evaluates answer
 ↓
Immediate Feedback
 ↓
Next Question
 ↓
Finish Quiz
 ↓
Calculate Score
 ↓
Save Attempt
 ↓
Show Result
```

---

# 12. Admin AI Flow

```text
Admin Dashboard
       ↓
Generate AI Quiz
       ↓
Select topic/difficulty/count
       ↓
AI generation
       ↓
Backend validation
       ↓
PENDING_REVIEW
       ↓
Admin reviews
       ↓
Edit / Approve / Reject
       ↓
PUBLISHED
```

---

# 13. API Structure

## Authentication

POST /api/auth/register

POST /api/auth/login

## Users

GET /api/users/me

GET /api/users/me/attempts

## Quizzes

GET /api/quizzes

GET /api/quizzes/{id}

POST /api/quizzes

PUT /api/quizzes/{id}

DELETE /api/quizzes/{id}

POST /api/quizzes/{id}/publish

POST /api/quizzes/{id}/unpublish

## Questions

POST /api/quizzes/{quizId}/questions

PUT /api/questions/{id}

DELETE /api/questions/{id}

## Attempts

POST /api/quizzes/{quizId}/attempts

POST /api/attempts/{attemptId}/answers

POST /api/attempts/{attemptId}/complete

GET /api/users/me/attempts

## AI

POST /api/ai/quizzes/generate

POST /api/ai/quizzes/personalized

POST /api/ai/quizzes/{id}/review

GET /api/admin/ai/quizzes/pending

POST /api/admin/ai/quizzes/{id}/approve

POST /api/admin/ai/quizzes/{id}/reject

---

# 14. Architectural Rules

- Controllers handle HTTP requests only.
- Services contain business logic.
- Repositories handle persistence.
- Entities represent database models.
- DTOs represent API requests/responses.
- Security logic stays inside security-related classes.
- AI provider classes only handle communication with the AI provider.
- AI cannot directly access repositories.
- AI cannot bypass validation.
- AI cannot determine authorization.
- AI cannot calculate official scores.
- Frontend communicates with backend through APIs.
