# OrderFlow OMS

OrderFlow is a distributed, event-driven **Order Management System (OMS)** inspired by real-world trading platforms.

The project demonstrates modern backend architecture principles including idempotent APIs, asynchronous processing, Kafka-based event streaming, and clean separation of concerns.

---

## 🚀 Architecture

REST API → PostgreSQL → Kafka → Async Execution Engine → Event Timeline

### Architectural Principles

- Non-blocking order submission
- Kafka-based decoupled execution pipeline
- Idempotent API design (exactly-once creation)
- Event-driven lifecycle tracking
- Optimistic concurrency control
- Fully Dockerized infrastructure

---

## 🧱 Tech Stack

### Backend
- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Docker / Docker Compose

### Frontend
- React
- Axios
- React Router

---

## 📦 Core Features

- Idempotent order placement via `Idempotency-Key`
- Database-enforced uniqueness guarantee
- Kafka-based asynchronous execution
- Strategy-based execution (Market / Limit)
- Complete order lifecycle management
- Event timeline (audit trail)
- Cancel flow with race-condition protection
- Pagination support
- Containerized deployment

---

## 🔐 Idempotency Model

OrderFlow guarantees **exactly-once order creation**.

How it works:

1. Client sends `Idempotency-Key` header.
2. A unique database constraint is enforced on `(user_id, idempotency_key)`.
3. If a duplicate request is received, the existing order is returned.
4. Execution is not triggered again.

This prevents:
- Duplicate trades
- Double-click issues
- Network retry duplication
- Concurrent submission race conditions

---

## 🔄 Order Lifecycle

CREATED  
↓  
VALIDATED  
↓  
SENT_TO_EXECUTOR  
↓  
EXECUTING  
↓  
EXECUTED / FAILED / CANCELLED  

Each transition is recorded in the **Order Event Timeline** for auditability.

---

## 🗂 Project Structure

orderflow/  
├── backend/  
├── frontend/  
└── docker-compose.yml  

---

## 🐳 Running Locally

Build and start all services:

docker compose up --build

Backend API:
http://localhost:8080/api/v1

Frontend:
http://localhost:3000

---

## 🔮 Future Enhancements

- Dead-letter queue (DLQ) for failed executions
- Retry with exponential backoff
- Outbox pattern for reliable event publishing
- Microservices decomposition
- Kubernetes deployment
- Observability (Prometheus, Grafana, distributed tracing)
- CQRS read model optimization

---

## 🎯 Project Vision

OrderFlow is designed to evolve from a monolithic asynchronous system into a scalable, cloud-native, event-driven OMS architecture — mirroring real-world financial trading systems.
