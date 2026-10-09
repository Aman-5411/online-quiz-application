# Online Quiz Application — Development Rules

These rules keep future maintenance safe and consistent with the delivered application.

## 1. Inspect Before Changing

Before changing code:
1. Read `Memory.md` for the current implementation state.
2. Read the relevant sections of `Architecture.md`, `PRD.md`, and `Rules.md`.
3. Inspect the existing files and current behavior.
4. Identify the smallest safe change.
5. Update documentation when behavior or configuration changes.

Do not assume a feature or endpoint exists without checking the code.

## 2. Technology and Dependencies

Keep the existing stack:
- Java 21 and Spring Boot
- Spring Security and JWT
- Spring Data JPA and Hibernate
- PostgreSQL
- Maven
- React and Vite

Do not replace the core stack or add a major dependency without a clear need. Prefer existing libraries and patterns.

## 3. Backend Structure

Keep responsibilities separated:

```text
Controller → Service → Repository → Database
```

- Controllers handle HTTP concerns.
- Services own business logic and validation.
- Repositories handle persistence.
- DTOs define safe API request/response shapes.
- Configuration and security concerns stay in their appropriate classes.
- Use constructor injection.
- Avoid duplicated business logic and unnecessary abstractions.

## 4. Security Rules

- Never store plaintext passwords.
- Never hardcode JWT signing secrets or database credentials.
- Validate JWTs on protected endpoints.
- Enforce authorization in the backend; hiding a frontend control is not security.
- Require ADMIN for administrative operations.
- Verify attempt ownership against the authenticated user.
- Do not trust a user ID supplied by the client as proof of ownership.
- Do not expose correct-answer fields in responses used to take a quiz.
- Do not expose stack traces, credentials, signing secrets, or internal configuration in API errors.
- Keep CORS restricted to the intended frontend origin.

## 5. Quiz and Attempt Rules

- Quiz publication state controls which quizzes are available to users.
- The quiz-taking workflow is single-choice.
- A single-choice question must not have multiple correct options.
- The backend is authoritative for answer evaluation, score, percentage, and attempt completion.
- The frontend timer is not the sole enforcement mechanism; the backend scheduler submits expired unfinished attempts.
- Preserve existing attempt ownership checks and administrative reporting restrictions when changing attempt workflows.
- Be careful when deleting production quizzes or attempts because related records may exist.

## 6. API and Error Handling

- Use REST-appropriate HTTP methods and status codes.
- Validate required fields, IDs, and allowed values.
- Use the existing centralized exception-handling approach.
- Return safe, understandable error messages.
- Do not invent endpoint paths; inspect controllers and frontend services before documenting or calling APIs.

## 7. Frontend Rules

- Use the existing API service rather than scattering base URLs through components.
- Read the API base URL from `VITE_API_BASE_URL`.
- Preserve the existing JWT request behavior and authentication handling.
- Keep UI styling consistent with the navy, white, and neutral visual theme.
- Avoid adding unused components, utilities, routes, or dependencies.
- Keep user-facing errors understandable.

## 8. Configuration and Deployment

Backend environment variables:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGIN`
- `PORT` where supplied by the hosting platform

Frontend environment variable:
- `VITE_API_BASE_URL`

Never commit `.env` files, credentials, JWT secrets, or database passwords. Remember that `VITE_*` values are exposed to the browser and must not contain secrets.

## 9. Testing and Change Verification

- Run relevant backend tests for backend changes.
- Run `mvn clean test` when making changes that affect backend behavior or configuration.
- Run `npm run build` after frontend changes.
- Frontend automated tests are not part of the current workflow.
- Do not claim a test passed unless it was actually run and passed.
- Check that existing workflows still work after a change.

## 10. Documentation and Handoff

After a significant change:
- Update `Memory.md` with what changed and what was verified.
- Update `README.md` if setup, deployment, or user-facing behavior changed.
- Update `Architecture.md`, `PRD.md`, or `Design.md` if the documented current system changed.
- List files changed, tests run, and any known issue.
- Keep documentation focused on implemented behavior; do not describe unbuilt features as available.
