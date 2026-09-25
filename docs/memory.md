# Project Memory

# Online Quiz Application

> This file is the persistent implementation memory for the project.
> Every AI coding assistant must read this file before making changes.
> Every AI coding assistant must update this file after completing a significant task or phase.
>
> This file records ACTUAL implementation progress. Do not mark planned work as completed.

---

# 1. Project Identity

Project:

Online Quiz Application

Purpose:

A full-stack quiz platform where users can register, authenticate, take quizzes, receive feedback, view scores, track quiz history, and optionally receive AI-generated personalized practice.

Primary Backend:

Java + Spring Boot

Database:

PostgreSQL

Frontend:

React

Authentication:

Spring Security + JWT

Build Tool:

Maven

AI:

External AI provider through a provider abstraction and validated backend workflow.

---

# 2. Current Development Phase

Current Phase:

PHASE 0 — PROJECT PLANNING

Current Status:

Planning documents completed. Implementation has not started.

Last Completed Phase:

Phase 0 — Project Planning

Next Phase:

Phase 1 — Database Design

---

# 3. Technology Decisions

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

PostgreSQL

## Frontend

React

## AI

AI provider is intentionally abstracted behind:

```text
AIQuizProvider
```

The concrete provider should be selected during implementation.

---

# 4. Architecture Decisions

The backend follows:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

DTOs are used for API requests and responses.

Entities represent database models.

Security logic is separated into security-related classes.

Business logic belongs in services.

AI integration is separated from core business logic.

AI providers must not directly access repositories.

---

# 5. Authentication Decisions

Authentication method:

JWT

Password hashing:

BCrypt

Roles:

- USER
- ADMIN

Important security rule:

A user must never be able to access another user's private quiz attempts simply by changing a user ID in a request.

AI must never handle authentication or authorization.

---

# 6. Quiz Source and Status

Quiz sources:

- MANUAL
- AI_GENERATED

Quiz statuses:

- DRAFT
- PENDING_REVIEW
- PUBLISHED
- REJECTED
- ARCHIVED

AI-generated quizzes normally begin as:

PENDING_REVIEW

Automatic publishing may be enabled later through explicit configuration.

---

# 7. AI Decisions

## Included AI Capabilities

Planned:

- AI quiz generation for Admins.
- Personalized quiz generation using controlled performance summaries.
- Optional AI quiz review.
- Optional automatic publishing.

## AI Must Not Control

- Authentication.
- Authorization.
- User roles.
- Official score calculation.
- Completed attempts.
- Database access.
- Security decisions.

## AI Data Minimization

Only required quiz-generation/performance information should be sent to the AI.

Never send:

- Passwords.
- JWTs.
- API keys.
- Authentication secrets.
- Unnecessary personal information.

## AI Validation

All AI-generated quiz output must be validated by the backend before storage.

---

# 8. Database Entities

Planned entities:

- User
- Quiz
- Question
- Option
- QuizAttempt
- QuizGenerationRequest

Relationships:

```text
User
 └── QuizAttempt

Quiz
 └── Question
      └── Option

Quiz
 └── QuizAttempt

QuizGenerationRequest
 └── generated Quiz
```

---

# 9. Implemented Features

## Authentication

Status: NOT STARTED

- [ ] Registration
- [ ] Password hashing
- [ ] Login
- [ ] JWT generation
- [ ] JWT validation
- [ ] Role-based authorization

## User Management

Status: NOT STARTED

- [ ] User profile
- [ ] Current user endpoint
- [ ] User data protection

## Manual Quiz Management

Status: NOT STARTED

- [ ] Create quiz
- [ ] Read quiz
- [ ] Update quiz
- [ ] Delete/archive quiz
- [ ] Publish/unpublish quiz

## Question Management

Status: NOT STARTED

- [ ] Create question
- [ ] Update question
- [ ] Delete question
- [ ] Manage options
- [ ] Correct answer management
- [ ] Explanation

## Quiz Attempt

Status: NOT STARTED

- [ ] Start attempt
- [ ] Submit answer
- [ ] Evaluate answer
- [ ] Immediate feedback
- [ ] Complete quiz

## Scoring

Status: NOT STARTED

- [ ] Correct answers
- [ ] Incorrect answers
- [ ] Score
- [ ] Percentage

## Progress

Status: NOT STARTED

- [ ] Attempt history
- [ ] Previous scores
- [ ] Performance summary

## AI Quiz Generation

Status: NOT STARTED

- [ ] AI provider abstraction
- [ ] AI provider implementation
- [ ] Generate quiz
- [ ] Parse structured response
- [ ] Validate generated quiz
- [ ] Save AI-generated quiz
- [ ] Track generation request
- [ ] Admin review

## Personalized AI Quizzes

Status: NOT STARTED

- [ ] Performance summary
- [ ] Weak-topic identification
- [ ] Personalized generation
- [ ] Validation
- [ ] Publication policy

## AI Review

Status: NOT STARTED

- [ ] Question review
- [ ] Quality suggestions
- [ ] Duplicate/similarity detection
- [ ] Admin accept/edit/reject

## AI Auto-Publishing

Status: NOT STARTED

- [ ] Configuration
- [ ] Validation
- [ ] Automatic publication
- [ ] Failure handling

## Leaderboard

Status: NOT STARTED

- [ ] Quiz leaderboard
- [ ] Overall leaderboard

---

# 10. Current Database Schema

Status:

NOT IMPLEMENTED

Planned structure:

```text
users
--------------------------------
id
name
email
password
role
created_at


quizzes
--------------------------------
id
title
description
category
difficulty
source
status
created_by
created_at
published_at


questions
--------------------------------
id
quiz_id
question_text
question_order
explanation
difficulty


options
--------------------------------
id
question_id
option_text
is_correct


quiz_attempts
--------------------------------
id
user_id
quiz_id
score
total_questions
correct_answers
incorrect_answers
percentage
started_at
completed_at


quiz_generation_requests
--------------------------------
id
requested_by
topic
category
difficulty
question_count
generation_reason
status
created_at
```

This is the planned schema and must not be treated as implemented until migrations are actually created and tested.

---

# 11. API Endpoints

These are planned endpoints. Update this section as implementation changes.

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

## AI

POST /api/ai/quizzes/generate

POST /api/ai/quizzes/personalized

POST /api/ai/quizzes/{id}/review

## Admin AI Management

GET /api/admin/ai/quizzes/pending

POST /api/admin/ai/quizzes/{id}/approve

POST /api/admin/ai/quizzes/{id}/reject

---

# 12. Files Created

Update this list as files are actually created.

## Root

- [x] PRD.md
- [x] Architecture.md
- [x] Rules.md
- [x] Phases.md
- [x] Design.md
- [x] Memory.md
- [ ] README.md

## Backend

- [ ] QuizApplication.java
- [ ] SecurityConfig.java
- [ ] JwtService.java
- [ ] JwtAuthenticationFilter.java
- [ ] AuthController.java
- [ ] QuizController.java
- [ ] QuestionController.java
- [ ] AttemptController.java
- [ ] AIQuizController.java
- [ ] AIQuizService.java
- [ ] AIQuizProvider.java

## Frontend

- [ ] App.jsx
- [ ] AuthContext.jsx
- [ ] Login page
- [ ] Register page
- [ ] Dashboard
- [ ] Quiz page
- [ ] Result page
- [ ] History page
- [ ] Admin dashboard
- [ ] AI Quiz Generator
- [ ] AI Review Queue

---

# 13. Important Decisions Log

Record important architectural decisions here.

Format:

Date:

Decision:

Reason:

Alternatives Considered:

Impact:

---

## Decision 1

Date:

2026-09-25

Decision:

Use Java 21 + Spring Boot for the backend.

Reason:

The project is intended to demonstrate Java backend development and Spring Boot skills.

Impact:

All backend implementation will follow the Spring Boot ecosystem.

---

## Decision 2

Date:

2026-09-25

Decision:

Add AI as a separate quiz-generation subsystem while retaining full Admin functionality.

Reason:

AI should automate quiz creation without replacing deterministic backend logic or Admin control.

Impact:

The architecture includes AIQuizService, AIQuizProvider, AI response validation, and AI-related quiz statuses.

---

## Decision 3

Date:

2026-09-25

Decision:

AI must not directly access the database or control authentication, authorization, scoring, or user roles.

Reason:

Security and business-critical operations must remain deterministic and under backend control.

Impact:

AI output must pass through backend validation and normal application services.

---

## Decision 4

Date:

2026-09-25

Decision:

AI-generated quizzes should support Admin approval and optional automatic publishing.

Reason:

This provides both human oversight and automation without forcing either workflow.

Impact:

Quiz status and publication policy must be represented in the backend.

---

# 14. Current Problems

Record unresolved problems here.

Format:

Problem:

Status:

Possible Cause:

Attempted Solutions:

Next Action:

---

Currently:

No known implementation problems because coding has not started.

---

# 15. Known Limitations

Currently:

- Implementation has not started.
- AI provider has not been selected/configured.
- Leaderboard is optional.
- Automatic AI publishing is not yet implemented.
- AI-generated content will require backend validation.
- Advanced features are not part of the initial MVP.

---

# 16. Testing Status

## Backend

- [ ] Unit tests
- [ ] Repository tests
- [ ] Service tests
- [ ] Controller tests
- [ ] Security tests
- [ ] Integration tests
- [ ] AI response validation tests
- [ ] AI provider failure tests

## Frontend

- [ ] Authentication flow
- [ ] Quiz flow
- [ ] Result flow
- [ ] History flow
- [ ] Admin flow
- [ ] AI generation flow

---

# 17. Last Completed Work

Date:

2026-09-25

Work:

Project planning documents created and updated to include AI quiz generation, personalized quizzes, optional AI review, and optional automatic publishing.

Status:

Completed.

---

# 18. Next Task

The next AI assistant should:

1. Read PRD.md.
2. Read Architecture.md.
3. Read Rules.md.
4. Read Phases.md.
5. Read Design.md.
6. Read this Memory.md.
7. Begin Phase 1 — Database Design.
8. Do not implement later phases prematurely.
9. Update this file after completing the phase.

---

# 19. AI Handoff Notes

Important:

Do not assume previous implementation work that is not recorded in this file.

Before modifying existing code:

1. Inspect the relevant files.
2. Check the current implementation.
3. Check this memory.
4. Make the smallest necessary change.
5. Test the change.
6. Update Memory.md.

If the existing implementation conflicts with this document, explain the conflict before making a major architectural change.

---

# 20. Last Updated

Date:

2026-09-25

Updated By:

Project AI Assistant

Reason:

Added AI quiz-generation architecture, AI boundaries, personalized quiz workflow, AI review, optional auto-publishing, and corresponding development phases.
