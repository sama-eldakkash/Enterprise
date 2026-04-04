# WorkHub — Phase 1 

Multi-tenant SaaS backend (Spring Boot 3). This release covers the **Phase 1** foundation: JWT auth with `tenantId` claim, tenant context, DTO validation, consistent JSON errors, and a demonstrated transactional rollback.

## Prerequisites

- Java 17+
- Maven (or use the included `mvnw` / `mvnw.cmd`)

## Run locally

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Default port: **8080**.

## Seeded data

On first startup an in-memory H2 database is initialized with:

| Field   | Value              |
|--------|--------------------|
| Tenant | Acme Corp (`STANDARD` plan), id `1` |
| Admin  | `admin@test.com` / `123456`, role `TENANT_ADMIN`, tenant `1` |

H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, user `sa`, empty password).

## Authentication

- **JWT** is required for every endpoint except `POST /auth/login`, `POST /auth/register`, and `/h2-console/**`.
- Send: `Authorization: Bearer <token>`.
- Tokens include claims: **subject** (email), **`tenantId`**, **`role`** (`TENANT_ADMIN` or `TENANT_USER`).

### Register (public)

Registration assigns the user to the tenant given by header **`X-Tenant-ID`** (required).

Example: register a user under tenant `1`:

```http
POST /auth/register
X-Tenant-ID: 1
Content-Type: application/json

{"name":"Pat","email":"pat@example.com","password":"secret12"}
```

### Login

```http
POST /auth/login
Content-Type: application/json

{"email":"admin@test.com","password":"123456"}
```

Response: `{ "accessToken": "...", "tokenType": "Bearer" }`.

### Current user + tenant

```http
GET /auth/me
Authorization: Bearer <token>
```

Returns JSON with `user` and `tenant` objects.

### Transaction rollback demo (admin only)

`POST /auth/register-fail` requires a **TENANT_ADMIN** JWT. It intentionally throws after `save()` so the transaction rolls back (no partial row). Use a unique email each attempt.

## API error format

Errors use a consistent JSON shape:

```json
{
  "timestamp": "2026-04-01T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "…",
  "details": []
}
```

`details` is populated for validation failures.

## Phase 1 deliverables

| Item | Location |
|------|-----------|
| Run instructions | This README |
| Postman | `postman/WorkHub-Phase1.postman_collection.json` |
| Design note  |DESIGN-NOTE.pdf` |

Git tag for submission (create after commit):

```bash
git tag -a v1-phase1-week7 -m "Phase 1 foundation"
```

## Project layout

- `controller` — REST layer  
- `service` — business logic, `@Transactional` boundaries  
- `repository` / `entity` — JPA  
- `dto` — request/response models and `ApiError`  
- `tenant` — `TenantFilter` + `TenantContext` (header; JWT overwrites when present)  
- `config` — Security, JWT, seed data  
