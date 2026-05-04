# WorkHub — Design Note (Phase 1)

*Export this document to PDF for the Phase 1 deliverable `DESIGN-NOTE.pdf` (2–3 pages).*

## 1. Architecture and layering

WorkHub follows a classic Spring Boot **layered architecture**:

- **Controllers** (`com.workhub.controller`) expose HTTP resources and map JSON DTOs. They do not contain business rules or persistence logic.
- **Services** (`com.workhub.service`) implement use cases, own **transaction boundaries** (`@Transactional`), and coordinate repositories.
- **Repositories** (`com.workhub.repository`) are Spring Data JPA interfaces for persistence.
- **Entities** (`com.workhub.entity`) map the relational model (shared database, `tenant_id` on tenant-scoped rows).
- **DTOs** (`com.workhub.dto`) isolate the API contract from entities and carry **validation** annotations (`jakarta.validation`).
- **Cross-cutting configuration** (`com.workhub.config`) centralizes security, JWT issuance/parsing, and bootstrap data.

Errors are normalized in **`GlobalExceptionHandler`** (`com.workhub.exception`) so clients always receive the same **`ApiError`** JSON structure (status, error code label, message, optional validation `details`).

## 2. Multi-tenancy approach (Phase 1)

**Strategy:** shared database, **shared schema**, with a **`tenant_id` column** on tenant-owned data (e.g. `users.tenant_id`). This matches the course minimum (“Shared DB + `tenant_id` + strict filtering”).

**Tenant resolution:**

1. **`TenantFilter`** (servlet filter, early order) reads optional header **`X-Tenant-ID`** and stores it in **`TenantContext`** (`ThreadLocal`). This supports **public registration**, where no JWT exists yet but the client must declare which organization the new user belongs to.
2. **`JwtAuthenticationFilter`** runs inside Spring Security. When a valid **Bearer** token is present, it builds the security principal and also sets **`TenantContext`** from the JWT **`tenantId` claim**. If both header and JWT are present, the **JWT wins**, reducing the risk of a client spoofing a different tenant on authenticated calls.

**Phase 1 scope:** tenant id is enforced on **registration** and carried in the token for authenticated requests. Full **read/write/list isolation** on every repository query is scheduled for Phase 2 (strict filtering everywhere).

## 3. Security model (Phase 1)

- **Stateless JWT:** no server session; `SessionCreationPolicy.STATELESS`.
- **Public endpoints:** `POST /auth/login`, `POST /auth/register`, and H2 console paths.
- **All other routes** require an **authenticated** principal derived from JWT.
- **Roles** in the token: `TENANT_ADMIN`, `TENANT_USER` (JWT claim `role`, mapped to Spring authorities `ROLE_*`).
- **Admin-only example:** `POST /auth/register-fail` is annotated with **`@PreAuthorize("hasRole('TENANT_ADMIN')")`** to demonstrate **RBAC** at the method level (full 401/403 matrix is hardened further in Phase 2).

## 4. Transaction boundary and rollback demonstration

**Use case:** user registration (`UserService.register` and the admin demo `registerWithFailure`).

- The service method is annotated with **`@Transactional`** (default propagation, rollback on **unchecked** exceptions).
- **Happy path:** validate tenant context → set `tenantId` and default role → **`save`** → commit.
- **Rollback path A:** if the email contains the substring `fail`, the method throws **`RuntimeException`** after `save`; the transaction **rolls back**, so **no row** remains.
- **Rollback path B:** `registerWithFailure` always throws after `save`; used to **prove** rollback with an admin-only endpoint in demos and tests.

This satisfies Phase 1’s requirement for at least **one multi-step transactional write** with a **clear rollback** story documented here and reproducible via the API/scripts.

## 5. Evolution (later phases)

Phase 2 will add strict tenant filters on all queries, messaging-based async jobs, and observability (Actuator, metrics, correlation IDs). Phase 3 adds Docker, Kubernetes, Terraform, and CI. The current design keeps **tenant id** in JWT and **`TenantContext`** so those layers can enforce isolation consistently without redesigning authentication.
