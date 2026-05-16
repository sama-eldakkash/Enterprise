# Observability Guide

## Spring Boot Actuator

The application exposes Actuator endpoints for monitoring and health checks.

---

## Health Endpoint

```bash
/actuator/health
```

Returns application health status.

---

## Readiness Probe

Configured in Kubernetes deployment:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
```

---

## Liveness Probe

Configured in Kubernetes deployment:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
```

---

## Metrics

Micrometer metrics are enabled through Spring Boot Actuator.

---

## RabbitMQ Monitoring

RabbitMQ Management UI:

```bash
http://localhost:15672
```

Default credentials:

- username: guest
- password: guest