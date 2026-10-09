# Online Quiz Application — Architecture

## 1. Overview

The application uses a client-server architecture. The React frontend communicates with a Spring Boot REST API. The backend applies authentication, authorization, validation, quiz business rules, and scoring before reading from or writing to PostgreSQL.

```text
Browser
  |
  v
React + Vite frontend (Vercel)
  |
  | HTTPS / REST / JSON
  v
Spring Boot backend (Render / Docker)
  |
  | Spring Data JPA / Hibernate
  v
PostgreSQL database (Supabase)
```

## 2. Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Security
- JWT authentication
- Spring Data JPA
- Hibernate
- Maven

### Frontend
- React
- Vite
- JavaScript
- CSS

### Hosting
- Vercel for the frontend
- Render for the backend container
- Supabase for PostgreSQL

## 3. Backend Layers

The backend follows a layered design:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

- **Controllers** expose REST endpoints and translate HTTP requests into service calls.
- **Services** enforce business rules, ownership checks, quiz workflows, and scoring.
- **Repositories** use Spring Data JPA to persist and retrieve entities.
- **Entities** represent persisted application data.
- **DTOs** shape API responses where data must be limited, especially for quiz-taking questions.
- **Security/configuration** handles JWT validation, endpoint access rules, CORS, and application settings.
- **Exception handling** provides centralized handling for API errors.

## 4. Main Domain Areas

- **User:** account details and role.
- **Quiz:** quiz metadata and publication status.
- **Question:** question text and association to a quiz.
- **Option:** answer choices and correctness data.
- **QuizAttempt:** a user's attempt, timing, and result.
- **QuizAnswer:** the selected option for a question in an attempt.

The backend uses a single-choice quiz workflow. The question service validates that a single-choice question has only one correct option.

## 5. Authentication and Authorization

1. The user registers or submits login credentials.
2. The backend validates the credentials.
3. On successful login, the backend issues a JWT.
4. The frontend includes the token in protected API requests.
5. The JWT authentication filter validates the token and establishes the authenticated principal.
6. Spring Security and service-level checks enforce roles and record ownership.

Administrative quiz mutations are restricted to ADMIN. Attempt retrieval checks that a normal user is accessing their own records; administrative reporting is restricted to ADMIN.

## 6. Quiz Attempt Flow

```text
User selects a published quiz
          ↓
Backend creates an attempt
          ↓
Frontend loads safe question/option responses
          ↓
User submits selected options
          ↓
Backend validates and stores answers
          ↓
User submits attempt, or timer expires
          ↓
Backend calculates and saves result
          ↓
Frontend displays result and attempt history
```

The frontend timer uses the attempt's backend `startedAt` value and configured duration. A scheduled backend task also finds expired unfinished attempts and submits them automatically. The backend remains authoritative for scoring and completion.

## 7. API Overview

All routes are rooted at `/api`.

| Area | Routes used by the application |
|---|---|
| Authentication | `/api/auth/**` |
| Published quizzes | `GET /api/quizzes/status/PUBLISHED` |
| Quiz management | `/api/quizzes/**` |
| Question and option management | `/api/questions/**`, `/api/options/**` |
| Start an attempt | `POST /api/attempts/user/{userId}/quiz/{quizId}` |
| Questions for an attempt | `GET /api/quizzes/{quizId}/questions/attempt` |
| Submit an answer | `POST /api/answers?attemptId={attemptId}&questionId={questionId}&optionId={optionId}` |
| Attempt answers | `/api/answers/attempt/{attemptId}` |
| Submit an attempt | `POST /api/attempts/{attemptId}/submit` |
| User attempt history | `GET /api/attempts/user/{userId}` |
| Admin attempt report | `GET /api/admin/attempts` |

Endpoint access depends on authentication, role, and ownership requirements.

## 8. Repository Layout

```text
online-quiz-application/
├── Backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── PRD.md
├── Architecture.md
├── Design.md
├── Rules.md
├── Phases.md
├── Memory.md
└── README.md
```

## 9. Production Configuration

The backend uses environment variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGIN`
- `PORT` (optional; defaults to `8080`)

The frontend uses:
- `VITE_API_BASE_URL`

Do not commit production secrets. Vite variables prefixed with `VITE_` are visible to browser code and must not contain secrets.
