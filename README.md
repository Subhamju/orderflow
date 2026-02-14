# OrderFlow OMS

OrderFlow is a distributed, event-driven Order Management System (OMS) inspired by real-world trading platforms.

It demonstrates modern backend system design principles including asynchronous execution, idempotent APIs, Kafka-based event streaming, optimistic concurrency control, and clean separation of concerns.

This project is designed to simulate the core of a trading backend system under realistic distributed conditions.

---

## 🚀 Architecture Overview

OrderFlow follows an event-driven architecture:

REST API  
→ Persist Order (PostgreSQL)  
→ Publish Event to Kafka  
→ Kafka Consumer Processes Execution  
→ Update Order State  
→ Record Immutable Event Timeline  

### Key Architectural Principles

- Non-blocking order submission
- Decoupled execution pipeline
- Event-driven processing via Kafka
- Idempotent API design
- Optimistic concurrency control
- Immutable event timeline (audit trail)
- Dockerized multi-service deployment

---

## 📊 Order Lifecycle

CREATED  
→ VALIDATED  
→ SENT_TO_EXECUTOR  
→ EXECUTING  
→ EXECUTED  

Or  

→ CANCELLED  
→ FAILED  

Each transition generates an immutable event stored in `order_events`.

---

## 📜 Event Timeline

Every order maintains a complete event history:

- ORDER_PLACED  
- SENT_TO_EXECUTOR  
- EXECUTING  
- EXECUTED  
- CANCEL_REQUESTED  
- CANCELLED  
- FAILED  

This provides full traceability similar to production trading systems.

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
- Kafka-based asynchronous execution
- Strategy-based execution (Market / Limit)
- Event timeline (audit log)
- Cancel flow with race-condition protection
- Optimistic locking (`@Version`)
- Paginated order listing
- Dockerized local infrastructure

---

## 🧠 Distributed Systems Concepts Demonstrated

- At-least-once event processing
- Idempotent API guarantees
- Async execution isolation
- Consumer group scalability
- Concurrency conflict handling
- Schema evolution considerations
- Service decoupling using message queues

---

## 🗂 Project Structure

orderflow/
├── backend/
│ ├── src/
│ └── README.md
├── frontend/
│ ├── src/
│ └── README.md
└── docker-compose.yml


---

## 🐳 Running Locally

```bash
docker compose up --build

---

## 🔐 Idempotency Model

- Order creation is idempotent for a given: (userId + Idempotency-Key)
- Guaranteed by:
   - Database-level unique constraint
   - Safe service-layer duplicate handling
- Ensures exactly-once order creation under retries.
