# TaskFlow API

Backend REST API for the TaskFlow SPA.

## Stack

- Java 17
- Spring Boot 3.2.x
- PostgreSQL
- Maven

## Run locally

1) Create the database and user:

```sql
create database taskflow;
create user taskflow with password 'taskflow';
grant all privileges on database taskflow to taskflow;
```

2) Start the app:

```bash
mvn spring-boot:run
```

3) Basic auth credentials:

- Username: `taskflow`
- Password: `taskflow`

## API endpoints

### Users

- `GET /api/users`

### Tasks

- `GET /api/tasks?status=TODO|IN_PROGRESS|DONE&priority=LOW|MEDIUM|HIGH&assignedToId=1`
- `GET /api/tasks/{id}`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

#### Task request body

```json
{
	"title": "Prepare weekly report",
	"description": "Summarize KPIs",
	"status": "TODO",
	"priority": "MEDIUM",
	"dueDate": "2026-02-20",
	"assignedToId": 1,
	"createdById": 2
}
```

## Database

The schema is created via Flyway at:

- [src/main/resources/db/migration/V1__init.sql](src/main/resources/db/migration/V1__init.sql)

Sample users are loaded from:

- [src/main/resources/data.sql](src/main/resources/data.sql)