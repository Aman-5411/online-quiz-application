# Online Quiz Application — Project Memory

> This file records the actual implementation and delivery state of the project.
> Read it before making future code changes. Update it after significant work.
> Do not describe unimplemented functionality as part of the delivered application.

## 1. Project Status

**Overall status: Completed and deployed for the current scope.**

- Frontend: Vercel
- Backend: Render
- Database: Supabase PostgreSQL
- Repository: https://github.com/Aman-5411/online-quiz-application
- Backend URL: https://online-quiz-application-vnln.onrender.com

The application has been deployed and the user confirmed that registration worked after the production CORS setting was corrected.

## 2. Technology Stack

- Java 21
- Spring Boot
- Spring Security
- JWT authentication
- Spring Data JPA / Hibernate
- Maven
- PostgreSQL
- React
- Vite
- JavaScript and CSS
- Docker for backend deployment
- Vercel, Render, and Supabase hosting

## 3. Implemented Features

### Authentication and Security
- Registration and login.
- JWT-based authentication.
- Role-based access controls.
- Admin-only quiz-management mutations.
- User attempt ownership checks.
- Centralized exception handling.
- Environment-based production configuration.
- CORS configuration for the frontend origin.

### Quiz Management
- Quiz listing and management.
- Quiz create/edit workflows for administrators.
- Question and option management.
- Published quiz listing.
- Single-choice validation.
- Safe question/option response DTOs for quiz taking.

### Attempts and Results
- Start quiz attempts.
- Submit answers.
- Submit attempts.
- Calculate and persist results.
- Show quiz result details.
- View user attempt history.
- Admin-wide attempt reporting.
- Search and result filtering in the admin attempts view.
- Automatic submission of expired attempts.

### Frontend
- Registration and login pages.
- User dashboard.
- Quiz attempt screen with timer.
- Result page.
- Attempt history page.
- Admin dashboard.
- Admin quiz management pages.
- Admin attempts page.
- Logo/favicon and application tab title.
- API base URL supplied by `VITE_API_BASE_URL`.

## 4. Production Configuration

### Backend environment variables

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGIN`
- `PORT` (optional; defaults to 8080)

The deployed backend uses the Supabase session pooler connection details. Do not put credentials or secret values in this file.

### Frontend environment variable

```env
VITE_API_BASE_URL=https://online-quiz-application-vnln.onrender.com/api
```

For local development, use:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

`VITE_*` variables are exposed to browser code; never place secrets in them.

## 5. Deployment Notes

- Render builds and runs the backend from `Backend/Dockerfile`.
- The backend connects to Supabase PostgreSQL through the session pooler.
- Vercel builds the Vite frontend from the `frontend/` root directory.
- `CORS_ALLOWED_ORIGIN` must match the exact deployed Vercel origin.
- `VITE_API_BASE_URL` must point to the deployed backend API base URL.
- Keep secrets in Render's environment configuration and local untracked environment variables.

## 6. Testing Status

Backend automated tests have been run and passed, including:
- Authentication service and controller tests.
- Quiz service and controller tests.
- Quiz attempt and answer service tests.
- Question service and controller tests.
- Repository tests.
- Security configuration and attempt ownership tests.
- Authentication and quiz workflow integration tests.
- User controller tests.

The full backend command `mvn clean test` passed after production configuration changes.

Frontend automated tests were intentionally skipped by decision. The frontend production build was verified using `npm run build`.

## 7. Important Implementation Details

- Quiz attempt duration is configured by `quiz.attempt.duration-minutes`, currently 10 minutes.
- Expired-attempt scheduler interval is configured by `quiz.attempt.scheduler-rate`, currently 600000 milliseconds (10 minutes).
- The frontend timer is based on backend `startedAt` and `durationMinutes`.
- `GET /api/admin/attempts` provides admin attempt reporting.
- Attempt ownership is enforced in the service layer.
- Quiz-taking responses avoid exposing the `correct` property before the appropriate result workflow.
- Published quiz deletion is restricted by current service logic. Clean up production test data carefully and consider related attempt/answer records before destructive database operations.

## 8. Current Delivery Checklist

- [x] Backend implemented.
- [x] Frontend implemented for the current scope.
- [x] Backend authentication and authorization.
- [x] Quiz management.
- [x] Attempt and scoring workflow.
- [x] User attempt history.
- [x] Admin attempt reporting.
- [x] Backend automated tests.
- [x] Frontend production build.
- [x] Production environment variables configured.
- [x] Backend deployed to Render.
- [x] Database hosted on Supabase.
- [x] Frontend deployed to Vercel.
- [x] Registration verified after CORS correction.
- [x] Project documentation updated.

## 9. Future Maintenance Rules

For any future change:
1. Inspect the existing code before editing.
2. Make the smallest appropriate change.
3. Run relevant tests/builds.
4. Preserve security and ownership checks.
5. Update this memory file and relevant documentation.
6. Report files changed and checks performed.

## 10. Last Updated

**Date:** 2026-10-09  
**Status:** Current project scope completed and deployed.
