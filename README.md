# UniHub

UniHub centralizes courses, enrollments, assignments, materials and academic deadlines. The project includes the V1 REST API and the first V2 React dashboard slice.

## Implemented

- Student registration and login with BCrypt passwords and signed JWTs
- Registration accepts university addresses and rejects common personal email providers
- Role-based authorization for `STUDENT`, `PROFESSOR` and `ADMIN`
- Courses with professor ownership checks
- Explicit many-to-many enrollment entity with a database uniqueness constraint
- Assignments ordered by deadline and classified as `URGENT`, `SOON` or `OK`
- Per-student assignment completion and dashboard progress by course
- Optional local demo catalog for a useful first-run experience
- PDF course materials stored locally with authenticated downloads
- Course prerequisites enforced during enrollment
- Weekly schedule and `.ics` deadline export
- In-app notifications for materials, assignments and approaching deadlines
- Course reviews restricted by enrollment age
- Course recommendations based on student co-enrollment
- PostgreSQL schema managed by Flyway instead of automatic Hibernate mutations
- Consistent validation and HTTP error responses

## Run locally

Requirements: Java 17 and PostgreSQL 15+. Maven is provided through the wrapper.

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/unihub"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="replace-with-a-random-secret-of-at-least-32-bytes"
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`. Run tests with `.\mvnw.cmd test`.

Start the React application in a second terminal:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

The UI is available at `http://localhost:5173`. Set `VITE_API_URL` to use a different API URL, `CORS_ALLOWED_ORIGINS` when frontend origins change, and `PERSONAL_EMAIL_DOMAINS` to configure blocked personal email providers.

## Main endpoints

| Method | Path | Access |
|---|---|---|
| POST | `/api/auth/register` | Public; creates a student |
| POST | `/api/auth/login` | Public |
| GET | `/api/courses` | Authenticated |
| POST | `/api/courses` | Professor or admin |
| POST | `/api/courses/{id}/enroll` | Student |
| GET | `/api/courses/{id}/students` | Course owner or admin |
| GET | `/api/courses/{id}/assignments` | Authenticated |
| POST | `/api/courses/{id}/assignments` | Course owner or admin |
| PUT | `/api/assignments/{id}/completion` | Enrolled student |
| DELETE | `/api/assignments/{id}/completion` | Student |
| GET | `/api/dashboard` | Student |
| GET | `/api/schedule` | Student |
| GET | `/api/calendar/export.ics` | Student |
| POST | `/api/courses/{id}/materials` | Course owner or admin |
| GET | `/api/materials/{id}/download` | Authenticated |
| POST | `/api/courses/{id}/prerequisites/{requiredId}` | Course owner or admin |
| POST | `/api/courses/{id}/schedule` | Course owner or admin |
| GET | `/api/notifications` | Authenticated |
| PATCH | `/api/notifications/{id}/read` | Notification owner |
| POST | `/api/courses/{id}/reviews` | Eligible enrolled student |
| GET | `/api/recommendations` | Student |
| GET | `/api/admin/users` | Admin |
| PATCH | `/api/admin/users/{id}/role` | Admin |
| DELETE | `/api/admin/courses/{id}` | Admin |

Send the token as `Authorization: Bearer <token>`. The first professor/admin must be promoted directly in PostgreSQL during local bootstrap; subsequent role changes are available through the admin endpoint.

## Structure

`controller -> service -> repository -> PostgreSQL`, with DTOs at the HTTP boundary and entities kept out of API responses. Database migrations live in `src/main/resources/db/migration`.

## Next milestone

V2 adds the React + TypeScript dashboard, assignment completion tracking, materials, calendar, unified search and internal notifications. Tests remain part of every milestone rather than being postponed until deployment.

### Email deadline reminders

UniHub checks every 15 minutes for assignments due in the next 72 hours and sends one reminder per student and assignment. Configure an SMTP account before enabling the job:

```powershell
$env:MAIL_ENABLED="true"
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-sender@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:MAIL_FROM="your-sender@gmail.com"
```

For Gmail, `MAIL_PASSWORD` must be an App Password, not the normal account password. Other university or Outlook SMTP servers can be used by changing the host and port.

For local development, use Mailpit instead of a real sender. Mailpit captures every message and exposes it at `http://127.0.0.1:8025`:

```powershell
$env:DB_PASSWORD="your-local-postgres-password"
.\scripts\start-local-mail.ps1
```

The `local-mail` profile checks for upcoming deadlines every minute. Messages remain local and are never delivered to real addresses.
