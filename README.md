# UniHub

**A full-stack academic workspace** that brings courses, assignments, deadlines, materials, schedules and notifications into one place for students, professors and administrators.

[![Watch the UniHub demo](docs/demo-preview.svg)](https://arianadaboc.github.io/UniHub/demo.html)

<p align="center">
  <a href="https://arianadaboc.github.io/UniHub/demo.html"><b>▶ Watch the demo</b></a> (5 min, plays in the browser)
  &nbsp;·&nbsp;
  <a href="docs/UniHub-Technical-Documentation.pdf"><b>📄 Technical documentation</b></a>
</p>

---

## Features

### For students
- Enrol in courses and track semester progress per course
- See upcoming deadlines, with priority labels (urgent / planned)
- Mark assignments as completed
- Download course materials (PDF)
- Weekly schedule and calendar view, with **ICS export** for Google Calendar / Outlook
- Review courses and receive in-app notifications
- Course recommendations based on what other students also enrolled in

### For professors and administrators
- Create courses, assignments and schedules
- Upload PDF materials and define course prerequisites
- Manage enrolled students
- Manage user roles and the academic catalogue (admin)

### Under the hood
- Registration restricted to **institutional email addresses** (personal domains such as Gmail or Yahoo are rejected)
- Course ownership checks, so professors can only edit their own courses
- Prerequisite validation before enrolment
- Deduplicated **email deadline reminders**, captured locally with Mailpit

## Tech stack

| Layer | Technologies |
|---|---|
| Frontend | React 18, TypeScript, Vite, Lucide icons |
| Backend | Java 17, Spring Boot, Spring Web, Spring Data JPA, Spring Mail |
| Security | Spring Security, JWT, role-based authorisation |
| Database | PostgreSQL, Flyway migrations |
| Testing | JUnit 5, Mockito, Spring Security Test, H2 |

## Architecture

```text
React + TypeScript (Vite, :5173)
          │  REST + JWT
          ▼
Spring Boot API (:8080)
  controllers → services → repositories
          │
          ▼
PostgreSQL (schema managed by Flyway)      Mailpit (local SMTP :1025)
```

## Getting started

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL running locally
- (optional) [Mailpit](https://github.com/axllent/mailpit) to see reminder emails

### 1. Create the database

```sql
CREATE DATABASE unihub;
```

Tables are created automatically by Flyway when the backend starts.

### 2. Start the backend

```bash
# defaults: jdbc:postgresql://localhost:5432/unihub, user postgres, password postgres
export DB_PASSWORD=your_password        # PowerShell: $env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

Useful environment variables:

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | local PostgreSQL | Database connection |
| `JWT_SECRET` | development key | Signing key for tokens (change it outside development) |
| `MAIL_ENABLED` | `false` | Turns deadline reminder emails on |
| `APP_SEED_DEMO_DATA` | `true` | Creates demo users and courses |

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open <http://localhost:5173>.

### Demo accounts

When demo data is enabled, these accounts are created:

| Role | Email | Password |
|---|---|---|
| Admin | `admin.demo@utcluj.ro` | `DemoAdmin2026!` |
| Professor | `profesor.demo@utcluj.ro` | `DemoProfessor2026!` |

Students can register with any institutional email address.

## Running the tests

```bash
mvn test
```

The suite covers JWT handling, the institutional email policy, assignments, progress tracking, materials, email reminders and repository queries.

## Project structure

```text
src/main/java/ro/utcluj/unihub/
  config/        demo data and app configuration
  controller/    REST endpoints
  dto/           request and response objects
  exception/     API error handling
  model/         JPA entities
  repository/    Spring Data repositories
  security/      JWT and Spring Security setup
  service/       business logic
src/main/resources/db/migration/   Flyway SQL migrations
frontend/                          React + TypeScript client
docs/                              technical documentation (PDF / HTML)
```

## Documentation

The [technical documentation](docs/UniHub-Technical-Documentation.pdf) covers the architecture, data model and role permissions in detail.
