# Project Development Phases

# Online Quiz Application

The project will be developed incrementally.

AI coding assistants must complete and verify one phase before moving to the next phase.

---

# Phase 0 — Project Planning

## Goal

Establish project structure and development standards.

### Tasks

- Finalize PRD.
- Finalize architecture.
- Finalize design.
- Create repository.
- Create README.
- Create Memory.md.
- Create backend project.
- Create frontend project.
- Configure Git.

### Completion Criteria

- Backend builds.
- Backend starts.
- Frontend starts.
- Repository structure is established.

---

# Phase 1 — Database Design

## Goal

Create the initial database structure.

### Tables/Entities

- users
- quizzes
- questions
- options
- quiz_attempts
- quiz_generation_requests

### Define

- Primary keys.
- Foreign keys.
- Unique constraints.
- Required fields.
- Relationships.
- Quiz source.
- Quiz status.

### Completion Criteria

- Database connects successfully.
- Schema is created successfully.
- Basic database operations work.

---

# Phase 2 — Authentication

## Goal

Implement secure user authentication.

### Tasks

- User registration.
- Password hashing.
- Login.
- JWT generation.
- JWT validation.
- Security filter.
- Role-based authorization.
- USER role.
- ADMIN role.

### Tests

- Successful registration.
- Duplicate email.
- Successful login.
- Invalid password.
- Missing token.
- Invalid token.
- USER accessing ADMIN endpoint.
- ADMIN accessing ADMIN endpoint.

### Completion Criteria

Authentication and authorization work correctly.

---

# Phase 3 — User Management

## Goal

Implement user-related functionality.

### Tasks

- Current user endpoint.
- User profile.
- User validation.
- Role handling.
- User ownership checks.

### Completion Criteria

Authenticated users can retrieve their own information.

Users cannot access another user's private information.

---

# Phase 4 — Quiz Management

## Goal

Allow administrators to manage quizzes manually.

### Tasks

- Create quiz.
- Get quiz.
- Get all quizzes.
- Update quiz.
- Delete/archive quiz.
- Category.
- Difficulty.
- Active status.
- Draft/published status.

### Authorization

ADMIN:

- Create.
- Update.
- Delete/archive.
- Publish/unpublish.

USER:

- View available published quizzes.

### Completion Criteria

Complete manual quiz CRUD functionality works.

---

# Phase 5 — Question Management

## Goal

Allow administrators to manage questions and options.

### Tasks

- Create question.
- Add options.
- Define correct answer.
- Add explanation.
- Update question.
- Delete question.
- Retrieve quiz questions.

### Security

Correct answers must not be exposed before submission.

### Completion Criteria

Admin can completely manage quiz questions.

Users can retrieve questions required for attempting quizzes.

---

# Phase 6 — Quiz Attempt System

## Goal

Allow users to take quizzes.

### Tasks

- Start attempt.
- Retrieve question.
- Submit answer.
- Evaluate answer.
- Immediate feedback.
- Move to next question.
- Complete attempt.

### Completion Criteria

A user can complete an entire quiz.

---

# Phase 7 — Scoring System

## Goal

Calculate and store quiz results.

### Tasks

- Correct answer count.
- Incorrect answer count.
- Total questions.
- Score.
- Percentage.
- Final result.

### Rule

Scoring must be deterministic backend logic.

AI must not calculate the official score.

### Completion Criteria

Scores are calculated accurately and stored.

---

# Phase 8 — Progress & History

## Goal

Allow users to track their performance.

### Tasks

- Attempt history.
- Individual attempt details.
- Score history.
- Quiz performance.
- Date/time of attempts.
- Performance summaries.

### Completion Criteria

Users can view their previous attempts.

Users cannot view another user's history.

---

# Phase 9 — Frontend Core

## Goal

Build the main user interface.

### Pages

- Login.
- Register.
- Dashboard.
- Quiz list.
- Quiz details.
- Quiz attempt.
- Result.
- History.
- Profile.

### Completion Criteria

A user can perform the complete quiz flow through the frontend.

---

# Phase 10 — Admin Dashboard

## Goal

Build the administrative interface.

### Pages

- Admin dashboard.
- Quiz management.
- Question management.
- Quiz creation.
- Quiz editing.
- Publish/unpublish.
- Attempt overview.

### Completion Criteria

Admin can manage quiz content through the frontend.

---

# Phase 11 — AI Quiz Generation

## Goal

Introduce the AI subsystem without making AI responsible for core business logic.

### Tasks

- Define AIQuizProvider interface.
- Implement AI provider integration.
- Create AI generation request DTOs.
- Generate structured quiz output.
- Parse AI response.
- Validate AI output.
- Store generated quiz.
- Track generation request.
- Add Admin AI quiz generator UI.

### Admin Flow

```text
Admin
 ↓
Select topic/difficulty/count
 ↓
Request AI quiz
 ↓
AI generation
 ↓
Backend validation
 ↓
PENDING_REVIEW
```

### AI Must Not

- Write directly to database.
- Change roles.
- Calculate scores.
- Bypass validation.

### Completion Criteria

Admin can generate an AI quiz and review it before publishing.

---

# Phase 12 — AI Quiz Review

## Goal

Allow AI to review existing quiz content.

### Possible Tasks

- Question clarity review.
- Duplicate/similarity detection.
- Difficulty suggestion.
- Option quality suggestions.
- Explanation suggestions.
- Ambiguity warnings.

### Flow

```text
Quiz
 ↓
AI Review
 ↓
Suggestions
 ↓
Admin
 ↓
Accept / Edit / Reject
```

AI suggestions must not automatically overwrite content.

### Completion Criteria

Admin can request an AI review and manually decide what to apply.

---

# Phase 13 — Personalized AI Quizzes

## Goal

Generate quizzes based on user performance.

### Tasks

- Analyze stored attempts.
- Create a minimal performance summary.
- Identify weak areas using deterministic backend aggregation.
- Send controlled summary to AI.
- Generate personalized quiz.
- Validate AI output.
- Store generated quiz.
- Apply configured publication policy.

### Example

```text
User Performance

Polymorphism: 40%
Interfaces: 30%
Inheritance: 90%

↓

AI

↓

Personalized Practice Quiz
```

### Completion Criteria

A user can receive a quiz targeted toward their weaker topics.

---

# Phase 14 — AI Auto-Publishing

## Goal

Support optional automatic publication of validated AI-generated quizzes.

### Modes

#### Admin Approval

```text
AI
 ↓
Validation
 ↓
PENDING_REVIEW
 ↓
Admin
 ↓
Approve
 ↓
PUBLISHED
```

#### Automatic

```text
AI
 ↓
Validation
 ↓
PUBLISHED
```

### Requirements

- Explicit configuration.
- Backend validation remains mandatory.
- AI cannot bypass application rules.
- Failed validation prevents publication.

### Completion Criteria

The application supports configurable AI publication behavior.

---

# Phase 15 — Leaderboard

## Goal

Implement optional leaderboard functionality.

### Tasks

- Quiz leaderboard.
- Overall leaderboard.
- Score-based ranking.
- Average score if required.

### Completion Criteria

Leaderboard data is calculated by backend logic.

---

# Phase 16 — Advanced Features

Only start after the core application and AI system are stable.

Possible features:

- Quiz timer.
- Random questions.
- Advanced filtering.
- Pagination.
- Search.
- Performance charts.
- Difficulty adaptation.
- User profile customization.
- Dark mode.

---

# Phase 17 — Testing

## Backend

- Unit tests.
- Repository tests.
- Service tests.
- Controller tests.
- Security tests.
- Integration tests.
- AI response validation tests.
- AI provider failure tests.

## Frontend

- Component testing.
- Authentication flow.
- Quiz flow.
- Result flow.
- History flow.
- Admin flow.
- AI generation flow.

## Security

Verify:

- Unauthorized access.
- Role escalation.
- User data isolation.
- Invalid JWT.
- Invalid input.
- Sensitive information exposure.
- AI prompt/input data minimization.
- AI output validation.

---

# Phase 18 — Deployment

## Backend

Deploy Spring Boot application.

## Database

Deploy PostgreSQL.

## Frontend

Deploy React application.

### Tasks

- Environment variables.
- Production configuration.
- CORS.
- Database connection.
- JWT secrets.
- AI API credentials.
- Build configuration.

Never commit secrets to GitHub.

---

# Phase 19 — Final Documentation

Create/update:

- README.md
- API documentation.
- Setup instructions.
- Database documentation.
- Architecture documentation.
- AI integration documentation.
- User guide.
- Known limitations.

---

# Definition of Done

A phase is complete only when:

- Code is implemented.
- Application builds.
- Relevant tests pass.
- Existing functionality still works.
- Security is verified.
- Documentation is updated.
- Memory.md is updated.
