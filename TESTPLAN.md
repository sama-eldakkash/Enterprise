# Enterprise Test Plan

## Tenant Isolation Tests

- Cross-tenant project access
- Cross-tenant task updates
- Cross-tenant project/task listing

---

## RBAC Tests

- Missing JWT returns 401
- Unauthorized role returns 403
- Admin role succeeds

---

## Transaction Rollback Test

- Multi-step operation rollback verification

---

## Concurrency Test

- Concurrent updates on shared resource
- Lost update prevention verification

---

## Messaging Reliability Test

- RabbitMQ async processing verification
- Retry/idempotency verification

---

## Observability Test

- /actuator/health endpoint returns UP
- readiness/liveness endpoints available

---

## CI Verification

All tests run automatically using GitHub Actions CI pipeline.