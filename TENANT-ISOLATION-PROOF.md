# Tenant Isolation Proof

## Isolation Strategy

The application uses a shared database with tenant filtering using tenant_id.

Every request extracts tenant information from the authenticated JWT token.

---

## Security Goal

Tenant A must never access Tenant B data.

---

## Verification Steps

### 1. Authenticate as Tenant A

```bash
POST /auth/login
```

Receive JWT token for Tenant A.

---

### 2. Create Project Under Tenant A

```bash
POST /projects
```

---

### 3. Authenticate as Tenant B

```bash
POST /auth/login
```

Receive JWT token for Tenant B.

---

### 4. Attempt Cross-Tenant Access

```bash
GET /projects/{id}
```

Expected result:

```text
403 Forbidden
```

or

```text
404 Not Found
```

depending on implementation.

---

## Result

Cross-tenant access is blocked successfully.