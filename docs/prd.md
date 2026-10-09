# Online Quiz Application — Product Requirements

## 1. Overview

The Online Quiz Application is a full-stack web application where users can create an account, sign in, browse published quizzes, take timed quizzes, view results, and review previous attempts. Administrators can manage quiz content and inspect quiz-attempt records.

The application is deployed with a React frontend on Vercel, a Spring Boot backend on Render, and PostgreSQL on Supabase.

## 2. User Roles

### User
- Register and sign in.
- View available published quizzes.
- Start a quiz attempt.
- Answer single-choice questions.
- Submit a quiz and view the result.
- Review previous attempts and scores.

### Administrator
- Access the admin dashboard.
- Create, edit, and manage quizzes.
- Manage quiz questions and answer options.
- Publish quizzes.
- View quiz attempts across users.

## 3. Implemented Functional Requirements

### Authentication and Authorization
- Users can register and log in.
- Authentication uses JWT.
- Passwords are handled by the backend's authentication implementation.
- Protected API endpoints require authentication.
- Administrative quiz-management operations require the ADMIN role.
- Users cannot access another user's private attempt data by changing a user ID in a request.

### Quiz Management
- Quizzes have details such as title, description, category, difficulty, source, and publication status.
- Administrators can create and manage quizzes and their questions/options.
- Published quizzes can be listed for users.
- Quiz-management operations are protected by backend authorization.

### Quiz Taking
- A user starts an attempt for a published quiz.
- The frontend loads quiz questions through the API.
- The user submits answers for individual questions.
- Quiz-taking responses do not expose correct-answer flags before submission.
- Attempts have a configured duration.
- Expired attempts are automatically submitted by a scheduled backend task.

### Results and History
- The backend calculates and stores attempt results.
- Results include score, total questions, correct answers, percentage, and timing information where available.
- Users can review their own attempts.
- Administrators can view all attempts through the admin attempts view.

### Administration
- The admin dashboard provides access to quiz management and attempt reporting.
- The admin attempts view supports searching and filtering attempt records.

## 4. Non-Functional Requirements

### Security
- Authentication and authorization are enforced by the backend.
- JWT signing secret and database credentials are supplied through environment variables.
- CORS is configured for the deployed frontend origin.
- Correct answers are not included in the quiz-taking question response.
- Ownership checks protect users' attempt records.

### Maintainability
- The backend uses a layered Spring architecture.
- Frontend API requests are centralized through the API service.
- Environment-specific values are configured outside source code.
- Backend automated tests cover services, controllers, repositories, security, and integration workflows.

### Usability
- The interface uses a restrained navy, white, and light-neutral visual theme.
- Forms show useful validation or error feedback.
- Main user and administrator workflows are accessible through dedicated pages.

## 5. Technology Stack

| Area | Technology |
|---|---|
| Backend | Java 21, Spring Boot |
| API and security | Spring Web, Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Backend build | Maven |
| Frontend | React, Vite, JavaScript, CSS |
| Frontend hosting | Vercel |
| Backend hosting | Render with Docker |
| Database hosting | Supabase |

## 6. Deployment

- Frontend: Vercel
- Backend: Render
- Database: Supabase PostgreSQL

The frontend reads its API base URL from `VITE_API_BASE_URL`. The backend reads `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `CORS_ALLOWED_ORIGIN` from the deployment environment.

## 7. Success Criteria

The delivered application is considered complete for its current scope when:
- A user can register and sign in on the deployed application.
- The frontend can communicate with the deployed backend.
- Users can browse published quizzes, take quizzes, and view results.
- Users can access their own attempt history.
- Administrators can manage quiz content and view attempts.
- Backend authorization and attempt ownership checks are enforced.
- The backend automated test suite passes.
- The application is deployed with externalized configuration.
