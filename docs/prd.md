# Product Requirements Document (PRD)

# Online Quiz Application

## 1. Project Overview

The Online Quiz Application is a full-stack web application that allows users to register, log in securely, browse quizzes, attempt multiple-choice questions, receive immediate feedback, and track their quiz performance.

The application has two primary roles:

- USER — takes quizzes and tracks personal performance.
- ADMIN — manages quizzes, questions, users, and AI-generated quiz content.

The application will also contain an AI Quiz System that can generate quizzes, personalize quizzes using controlled performance summaries, and optionally review or automatically publish generated quizzes.

The goal is to build a secure, maintainable, interview-ready application demonstrating Java, Spring Boot, REST APIs, PostgreSQL, authentication, authorization, frontend development, and practical AI integration.

---

## 2. Target Users

### 2.1 Regular Users

Users should be able to:

- Create an account.
- Log in securely.
- Browse available quizzes.
- Filter quizzes by topic/category and difficulty.
- View quiz details.
- Start a quiz.
- Answer questions one at a time.
- Receive immediate feedback.
- Complete a quiz.
- View final scores.
- View previous attempts.
- Track performance.
- Receive or access personalized AI-generated practice quizzes when enabled.

### 2.2 Administrators

Administrators should be able to:

- Log in using an administrator account.
- Create quizzes manually.
- Edit quizzes.
- Delete quizzes.
- Add, edit, and delete questions.
- Define correct answers.
- Set categories and difficulty.
- Publish/unpublish quizzes.
- Review AI-generated quizzes.
- Edit AI-generated quizzes.
- Approve or reject AI-generated quizzes.
- Configure whether AI-generated quizzes require approval.
- View quiz and attempt information.

---

## 3. Core Features

### 3.1 Authentication

- User registration.
- User login.
- Secure password hashing.
- JWT-based authentication.
- Role-based authorization.
- Protected API endpoints.
- Secure logout/client-side authentication handling.
- Request validation.

Passwords must never be stored in plain text.

---

### 3.2 User Management

Each user should have:

- Unique ID.
- Name.
- Email/username.
- Hashed password.
- Role.
- Account creation timestamp.

Roles:

- USER
- ADMIN

The backend must enforce ownership of private user data.

---

### 3.3 Quiz Management

Each quiz should contain:

- Quiz ID.
- Title.
- Description.
- Category/topic.
- Difficulty.
- Questions.
- Source.
- Publication status.
- Creation timestamp.
- Optional creator information.
- Publication timestamp.

Possible quiz sources:

- MANUAL
- AI_GENERATED

Possible statuses:

- DRAFT
- PENDING_REVIEW
- PUBLISHED
- REJECTED
- ARCHIVED

Administrators can:

- Create quizzes.
- Read quizzes.
- Update quizzes.
- Delete/archive quizzes.
- Publish/unpublish quizzes.

---

### 3.4 Question Management

Each question should contain:

- Question ID.
- Question text.
- Options.
- Correct answer(s).
- Question order.
- Optional explanation.
- Difficulty.

The initial implementation will focus on multiple-choice questions.

The architecture should allow future support for:

- Single-answer questions.
- Multiple-answer questions.

Correct answers must never be exposed to a user before answer submission.

---

## 4. Quiz Taking

When a user starts a quiz:

1. The server creates an attempt.
2. Questions are loaded.
3. The user answers each question.
4. The answer is submitted.
5. The backend evaluates the answer.
6. Immediate feedback is returned.
7. The score is updated.
8. The attempt is completed when the quiz finishes.
9. The final result is stored.

The official score must always be calculated by deterministic backend logic, not by AI.

---

## 5. Scoring and Progress

The application should:

- Calculate the score automatically.
- Track correct answers.
- Track incorrect answers.
- Calculate percentage.
- Store final results.
- Track attempts.
- Provide performance history.

Example:

Total Questions: 10
Correct Answers: 8
Incorrect Answers: 2
Score: 80%

The AI may analyze a controlled performance summary, but it must never modify the official score or attempt record.

---

# 6. AI Quiz System

The AI system is an additional subsystem. It does not replace the backend's business logic, security, or Admin functionality.

## 6.1 AI Quiz Generation

Administrators can request an AI-generated quiz using parameters such as:

- Topic.
- Category.
- Difficulty.
- Number of questions.
- Question type.
- Optional focus topics.

Example:

Topic: Java
Difficulty: Intermediate
Questions: 10
Type: Multiple Choice

The AI can generate:

- Quiz title.
- Description.
- Questions.
- Options.
- Correct answers.
- Explanations.
- Suggested category.
- Suggested difficulty.

The AI response must be structured and validated by the backend before storage.

---

## 6.2 Personalized AI Quiz Generation

The system may generate practice quizzes based on a user's quiz performance.

The AI should receive only the minimum required performance summary.

Example:

Topic: Java OOP
Score: 60%
Weak areas:
- Polymorphism
- Interfaces
- Abstract Classes

The AI can generate a new practice quiz focused on those areas.

The AI must not receive unnecessary personal information such as passwords, authentication tokens, or unrelated private data.

---

## 6.3 AI Quiz Review

Optional functionality:

AI can review questions and provide suggestions about:

- Clarity.
- Potential ambiguity.
- Duplicate/similar questions.
- Difficulty.
- Option quality.
- Explanation quality.

AI suggestions must not automatically overwrite Admin-created content.

The Admin should be able to:

- Accept.
- Edit.
- Reject.

---

## 6.4 AI Publishing

AI-generated quizzes can support two modes.

### Admin Approval Mode

```text
AI generates
    ↓
PENDING_REVIEW
    ↓
Admin reviews
    ↓
APPROVE / EDIT / REJECT
    ↓
PUBLISHED
```

### Automatic Publishing Mode

```text
AI generates
    ↓
Backend validation
    ↓
PUBLISHED
```

Automatic publishing should only occur when enabled by the application's configuration.

---

# 7. AI Boundaries

AI CAN:

- Generate quiz questions.
- Generate options.
- Generate explanations.
- Suggest categories.
- Suggest difficulty.
- Generate personalized practice quizzes.
- Analyze supplied quiz-performance summaries.
- Review quiz content and provide suggestions.

AI CANNOT:

- Authenticate users.
- Authorize requests.
- Change user roles.
- Create Admin accounts.
- Calculate the official score.
- Modify completed attempts.
- Directly access the database.
- Directly execute database operations.
- Bypass backend validation.
- Access passwords or authentication tokens.
- Automatically overwrite Admin content.
- Make security decisions.

The backend remains the source of truth.

---

# 8. Leaderboard

Leaderboard functionality is optional for the initial version.

If implemented, it may support:

- Quiz-specific leaderboard.
- Overall leaderboard.
- Score-based ranking.
- Average score.

Leaderboard calculations must be performed by backend logic.

---

# 9. Optional Advanced Features

Only after the MVP is stable:

- Quiz timer.
- Random question selection.
- Difficulty levels.
- Search.
- Pagination.
- Advanced filtering.
- Performance analytics.
- Admin dashboard.
- User profile.
- AI quiz review.
- Automated personalized quiz generation.
- AI auto-publishing.
- Dark mode.

---

# 10. Non-Functional Requirements

## Security

- Passwords must be hashed.
- JWT must be used for authentication.
- Protected endpoints require authentication.
- Admin endpoints require ADMIN authorization.
- Users cannot access another user's private history.
- Input must be validated.
- AI must not receive unnecessary private information.
- AI-generated content must pass backend validation.
- Secrets must be stored in environment variables.

## Performance

- Avoid unnecessary database queries.
- Use pagination for large datasets.
- Use proper indexes.
- Avoid unnecessary entity loading.
- AI calls should not block unrelated application functionality where practical.

## Maintainability

- Follow layered architecture.
- Keep controllers thin.
- Put business logic in services.
- Use repositories for database access.
- Use DTOs for API communication.
- Keep AI integration behind a dedicated service/provider abstraction.

---

# 11. MVP

The first stable version must contain:

1. User registration.
2. User login.
3. JWT authentication.
4. Role-based authorization.
5. Admin quiz CRUD.
6. Question management.
7. Quiz listing.
8. Quiz attempts.
9. Answer evaluation.
10. Score calculation.
11. Attempt history.

Recommended first AI feature:

12. Admin-triggered AI quiz generation.

Personalized AI quizzes, AI review, and automatic publishing should be implemented only after the core system is stable.

---

# 12. Success Criteria

The project is successful when:

- Users can register and log in.
- Authentication works securely.
- Admins can create and manage quizzes.
- Users can take quizzes.
- Answers are evaluated correctly.
- Scores are calculated correctly.
- Attempts are stored.
- Users can view their own history.
- Unauthorized users cannot access protected resources.
- Admins can generate AI quizzes.
- AI-generated content is validated before storage.
- AI does not control security or official scoring.
- The application can be run locally using documented instructions.
