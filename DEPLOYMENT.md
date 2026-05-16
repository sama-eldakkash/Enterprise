# WorkHub Deployment Guide

## Local Deployment with Docker Compose

### Requirements

- Docker Desktop
- Docker Compose
- Java 17
- Maven

---

## Build the Application

```bash
mvn clean package
```

---

## Run with Docker Compose

```bash
docker compose up --build
```

---

## Services

| Service | Port |
|---|---|
| Spring Boot App | 8080 |
| PostgreSQL | 5432 |
| RabbitMQ | 5672 |
| RabbitMQ Management | 15672 |

---

## Health Check

```bash
http://localhost:8080/actuator/health
```

---

# Kubernetes Deployment

## Requirements

- Minikube
- kubectl

---

## Start Minikube

```bash
minikube start --driver=docker --memory=3000 --cpus=2
```

---

## Apply Kubernetes Resources

```bash
kubectl apply -f k8s/
```

---

## Verify Pods

```bash
kubectl get pods
```

---

## Verify Services

```bash
kubectl get svc
```

---

## Access Application

```bash
minikube service workhub-app-service
```

---

# Terraform Deployment

## Initialize Terraform

```bash
terraform init
```

---

## Validate Configuration

```bash
terraform validate
```

---

## Preview Infrastructure Changes

```bash
terraform plan
```

---

# CI/CD

GitHub Actions pipeline automatically performs:

- Maven build
- Test execution
- Docker image build

on every push to the repository.