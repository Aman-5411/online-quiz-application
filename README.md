# Online Quiz Application

A full-stack web application for creating, managing, and taking quizzes.
It combines a React frontend with a Spring Boot REST API, JWT
authentication, role-based authorization, quiz attempts, scoring, and
progress tracking.

## Live Demo

-   **Frontend:** Add your Vercel URL here
-   **Backend API:** https://online-quiz-application-vnln.onrender.com
-   **Database:** PostgreSQL hosted on Supabase

> The backend requires authentication for most endpoints, so opening the
> base URL in a browser is not a complete health check.

## Features

### User

-   Register and log in.
-   Browse published quizzes.
-   Start a quiz attempt and submit answers.
-   View scores, percentages, and quiz results.
-   Review previous attempts and track progress.

### Admin

-   Use the admin dashboard.
-   Create and manage quizzes.
-   Add questions and answer options.
-   Publish quizzes.
-   View quiz attempts and results.

### Security and reliability

-   JWT-based authentication.
-   Role-based access control for administrator operations.
-   Quiz-taking responses do not expose correct answers before
    submission.
-   Automatic submission of expired quiz attempts.
-   Input validation and centralized exception handling.

## Technology Stack

  Area               Technologies
  ------------------ ---------------------------------------
  Frontend           React, Vite, JavaScript, CSS
  Backend            Java 21, Spring Boot, Spring Security
  Authentication     JWT
  Persistence        Spring Data JPA, Hibernate
  Database           PostgreSQL
  Frontend hosting   Vercel
  Backend hosting    Render (Docker)
  Database hosting   Supabase

## Project Structure

``` text
online-quiz-application/
├── Backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
└── frontend/
    ├── public/
    ├── src/
    ├── package.json
    └── vite.config.js
```

## Run Locally

### Prerequisites

-   Java 21
-   Maven
-   Node.js and npm
-   PostgreSQL
-   Git

### 1. Clone the repository

``` bash
git clone https://github.com/Aman-5411/online-quiz-application.git
cd online-quiz-application
```

### 2. Configure the backend

Set these environment variables before starting Spring Boot:

  ---------------------------------------------------------------------------------------------------
  Variable                Purpose                 Local example
  ----------------------- ----------------------- ---------------------------------------------------
  `DB_URL`                JDBC connection URL     `jdbc:postgresql://localhost:5432/online_quiz_db`

  `DB_USERNAME`           Database username       `postgres`

  `DB_PASSWORD`           Database password       Your local PostgreSQL password

  `JWT_SECRET`            Secret used to sign     A long, random secret of at least 32 bytes
                          JWTs                    

  `CORS_ALLOWED_ORIGIN`   Allowed frontend origin `http://localhost:5173`
  ---------------------------------------------------------------------------------------------------

Create a PostgreSQL database first, for example `online_quiz_db`. Use
your own credentials and a unique secret. Never commit real credentials
or production secrets.

In **PowerShell**, set variables for the current terminal session:

``` powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/online_quiz_db"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="YOUR_LOCAL_DB_PASSWORD"
$env:JWT_SECRET="YOUR_LONG_RANDOM_SECRET"
$env:CORS_ALLOWED_ORIGIN="http://localhost:5173"
```

Run the backend:

``` powershell
cd Backend
mvn spring-boot:run
```

The backend should start at `http://localhost:8080`.

### 3. Configure the frontend

Create `frontend/.env.local` with:

``` env
VITE_API_BASE_URL=http://localhost:8080/api
```

Install dependencies and start the development server:

``` bash
cd frontend
npm install
npm run dev
```

Vite normally serves the frontend at `http://localhost:5173`.

Create a production build locally with:

``` bash
npm run build
```

The build output is generated in `frontend/dist/`.

## Deployment

The project uses: - **Vercel** for the frontend, with `frontend` as the
Root Directory and the Vite preset. - **Render** for the backend, using
Docker and `Backend/Dockerfile`. - **Supabase** for PostgreSQL.

### Vercel environment variable

Configure this in the Vercel project settings:

``` env
VITE_API_BASE_URL=https://online-quiz-application-vnln.onrender.com/api
```

### Render environment variables

Configure these in the Render service settings:

``` text
DB_URL=jdbc:postgresql://YOUR_SUPABASE_POOLER_HOST:5432/postgres
DB_USERNAME=YOUR_SUPABASE_POOLER_USERNAME
DB_PASSWORD=YOUR_SUPABASE_DATABASE_PASSWORD
JWT_SECRET=YOUR_LONG_RANDOM_SECRET
CORS_ALLOWED_ORIGIN=YOUR_VERCEL_FRONTEND_ORIGIN
```

Use the exact connection details provided by Supabase. If using the
Supabase session pooler, use its specified host and username. Set
`CORS_ALLOWED_ORIGIN` to the exact deployed frontend origin, including
`https://` and without a trailing path.

After changing environment variables, redeploy or restart the relevant
service if it does not deploy automatically. Do not put secrets in
frontend environment variables: Vite variables prefixed with `VITE_` are
exposed to browser code.

## API Overview

The REST API is rooted at `/api`.

  -----------------------------------------------------------------------
  Area                    Example route           Purpose
  ----------------------- ----------------------- -----------------------
  Authentication          `/api/auth/**`          Registration and login

  Quizzes                 `/api/quizzes`          Retrieve and manage
                                                  quizzes

  Questions               `/api/questions/**`     Manage quiz questions
                                                  and options

  Attempts                `/api/attempts/**`      Start, retrieve, and
                                                  submit quiz attempts

  Answers                 `/api/answers/**`       Submit and retrieve
                                                  attempt answers

  Admin attempts          `/api/admin/attempts`   Retrieve attempts for
                                                  administration
  -----------------------------------------------------------------------

Exact HTTP methods and access requirements depend on the endpoint.
Administrative operations require an administrator role; protected
endpoints require a valid JWT.

## Testing

To run backend tests:

``` bash
cd Backend
mvn clean test
```

Frontend automated tests are not currently part of the project workflow.

## Security Notes

-   Keep `.env` files, database passwords, and JWT signing secrets out
    of version control.
-   Use a unique, strong `JWT_SECRET` in production.
-   Configure CORS to allow only the intended frontend origin.
-   Never share access tokens or production credentials in screenshots,
    logs, or issue reports.
-   Use test data during development and remove production test records
    carefully.

## Contributing

Contributions and suggestions are welcome.

1.  Fork the repository.
2.  Create a branch for your change.
3.  Make and test your changes.
4.  Open a pull request describing the change.

Avoid committing secrets, build output, `node_modules`, or local
environment files.

## License

No license has been specified yet. Unless a license is added to this
repository, all rights remain with the copyright holder and reuse is not
automatically granted.
