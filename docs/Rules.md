# AI Development Rules

# Online Quiz Application

This document defines the rules that every AI coding assistant must follow while working on this project.

---

# 1. General Rule

Before modifying code, read:

1. PRD.md
2. Architecture.md
3. Design.md
4. Phases.md
5. Memory.md

Do not introduce changes that conflict with these documents without explicitly discussing the change first.

---

# 2. Technology Rules

Use:

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- JWT
- React

Avoid replacing the core stack without approval.

Do not add another major framework without a clear requirement.

Do not add unnecessary libraries.

---

# 3. Dependency Rules

Before adding a dependency:

1. Check whether existing Spring Boot functionality solves the problem.
2. Check whether the feature can be implemented with existing dependencies.
3. Add a new dependency only when there is a clear reason.
4. Document unusual dependencies.

Avoid dependency bloat.

AI SDK dependencies should be isolated to the AI integration layer.

---

# 4. Code Structure

Follow:

Controller
    ↓
Service
    ↓
Repository

Do not:

- Put business logic inside controllers.
- Access repositories directly from controllers.
- Put database logic inside frontend code.
- Put AI-provider-specific logic throughout the application.
- Duplicate business logic.

---

# 5. Security Rules

Passwords must never be stored as plain text.

Use a secure password hashing mechanism such as BCrypt.

JWT must be validated on protected endpoints.

Authorization must be enforced by the backend.

Never trust a user ID supplied by the frontend when determining ownership.

The backend must verify that the authenticated user is allowed to access requested data.

Admin functionality must require ADMIN authorization.

AI must never be responsible for:

- Authentication.
- Authorization.
- Role assignment.
- Security decisions.
- Token generation.
- Password handling.

---

# 6. AI Rules

AI is an assistant subsystem, not the source of truth.

AI may:

- Generate quiz content.
- Generate options.
- Generate explanations.
- Suggest categories.
- Suggest difficulty.
- Generate personalized practice quizzes.
- Analyze supplied performance summaries.
- Review quiz content and provide suggestions.

AI may NOT:

- Directly access the database.
- Execute database queries.
- Modify database records directly.
- Change user roles.
- Create Admin accounts.
- Authenticate users.
- Authorize requests.
- Calculate official scores.
- Modify completed quiz attempts.
- Modify official user performance.
- Access passwords.
- Access JWTs or authentication secrets.
- Automatically overwrite Admin-created content.
- Bypass backend validation.

All AI output must pass backend validation before persistence.

---

# 7. AI Data-Minimization Rule

Only send the minimum information required to the AI.

For personalized generation, prefer:

```text
Topic
Quiz category
Difficulty
Score
Correct/incorrect topic summary
Weak areas
Question count
```

Do not send:

- Passwords.
- JWTs.
- API keys.
- Authentication tokens.
- Unnecessary personal information.
- Private information unrelated to quiz generation.

---

# 8. AI Output Validation

Never trust AI output blindly.

Validate:

- Required fields.
- Question count.
- Question text.
- Option count.
- Correct-answer existence.
- Correct-answer membership in options.
- Difficulty.
- Category.
- Maximum lengths.
- Duplicate questions where applicable.
- Supported question type.

If validation fails:

1. Do not save the quiz.
2. Log an appropriate technical error.
3. Return a safe error to the caller.
4. Optionally retry using controlled retry rules.

---

# 9. AI Publishing Rules

AI-generated quizzes should normally start as:

PENDING_REVIEW

unless automatic publishing is explicitly enabled.

Admin approval mode:

```text
AI → Validation → PENDING_REVIEW → Admin → Published
```

Automatic mode:

```text
AI → Validation → Published
```

Automatic publishing must never bypass backend validation.

The configuration for automatic publishing must be explicit.

---

# 10. Scoring Rules

Official scoring must be deterministic.

The backend calculates:

```text
score
correct answers
incorrect answers
percentage
```

AI must never determine the official score.

---

# 11. Validation Rules

Validate:

- Email.
- Password.
- Required fields.
- Quiz title.
- Question text.
- Options.
- Score-related values.
- IDs.
- Numeric values.
- AI generation requests.

Invalid input must return an appropriate HTTP status.

---

# 12. Error Handling

Use centralized exception handling.

Recommended exceptions:

- ResourceNotFoundException
- BadRequestException
- UnauthorizedException
- ForbiddenException
- AIProviderException
- AIResponseValidationException

API errors should use a consistent format.

Example:

```json
{
  "timestamp": "2026-09-25T10:00:00",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Generated quiz contains an invalid correct answer",
  "path": "/api/ai/quizzes/generate"
}
```

Never expose:

- Stack traces.
- Passwords.
- JWT secrets.
- API keys.
- Database credentials.
- Internal provider details.

---

# 13. Database Rules

Use JPA/Hibernate for normal persistence.

Use database constraints where appropriate.

Important constraints:

- Unique email.
- Non-null required fields.
- Foreign keys.
- Valid enum/state values.
- Valid score ranges.

Do not allow AI output to bypass database constraints.

---

# 14. API Rules

Use RESTful endpoint naming.

Use appropriate methods:

GET
POST
PUT/PATCH
DELETE

Use appropriate HTTP status codes:

200 — successful request
201 — resource created
400 — invalid request
401 — authentication required/invalid
403 — insufficient permissions
404 — resource not found
409 — conflict
500 — unexpected server error
502/503 — external AI provider/service issue where appropriate

---

# 15. Coding Rules

Use:

- Meaningful variable names.
- Small focused methods.
- Single responsibility.
- Clear class names.
- Java naming conventions.
- Constructor injection.
- DTOs for API boundaries.

Avoid:

- Huge methods.
- Duplicate code.
- Magic numbers.
- Unnecessary abstractions.
- Dead code.
- Commenting obvious code.

---

# 16. AI Provider Abstraction

Do not couple application business logic directly to one AI SDK.

Prefer:

```java
public interface AIQuizProvider {

    GeneratedQuiz generateQuiz(
        QuizGenerationRequest request
    );
}
```

Then implement provider-specific classes behind the interface.

This allows:

- Provider replacement.
- Mock testing.
- Easier integration tests.
- Lower coupling.

---

# 17. AI Behavior Rules

The AI coding assistant should:

- Read existing code before modifying it.
- Inspect the current schema before changing entities.
- Check existing dependencies.
- Make small changes.
- Test changes.
- Preserve working functionality.
- Explain important architectural changes.
- Update Memory.md after significant work.
- Report files changed.
- Report tests performed.
- Report known limitations.

The AI coding assistant must NOT:

- Rewrite the entire project unnecessarily.
- Change architecture without approval.
- Delete working code without reason.
- Invent APIs that do not exist.
- Assume database schema without checking it.
- Change technology stack casually.
- Implement future phases prematurely.
- Claim code works without testing.
- Add AI functionality to core authentication/security logic.

---

# 18. Scope Rule

Only work on the current phase unless explicitly instructed otherwise.

If a requested feature belongs to a later phase:

1. Mention it.
2. Do not implement it automatically.
3. Continue with the current phase.

---

# 19. Change Management

Before a major architectural change, explain:

- Current approach.
- Proposed approach.
- Why it is necessary.
- Advantages.
- Possible disadvantages.
- Files affected.

Wait for approval for major changes.

---

# 20. Testing Rules

Every major feature should have tests.

Test at minimum:

- Success case.
- Invalid input.
- Unauthorized access.
- Forbidden access.
- Resource not found.
- Important business logic.
- AI response validation.
- AI provider failure handling.

AI integration tests should support a mock provider so normal test execution does not require real AI API calls.

---

# 21. Documentation Rules

When behavior changes, update the relevant documentation.

Important files:

- README.md
- Memory.md
- API documentation when applicable.

---

# 22. Priority Order

When requirements conflict:

1. Security
2. Correctness
3. PRD
4. Architecture
5. Maintainability
6. Performance
7. AI convenience
8. UI improvements
9. Optional features

Never sacrifice security or correctness for AI convenience.
