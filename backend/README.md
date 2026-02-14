
---

# 📘 2️⃣ BACKEND README.md (Updated & Polished)

```markdown
# OrderFlow Backend

OrderFlow backend is a distributed Order Management System (OMS) built using Spring Boot and Kafka.

It demonstrates asynchronous execution, event-driven architecture, idempotent API design, and concurrency-safe state transitions.

---

## 🧱 Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Apache Kafka
- Docker
- Maven

---

## 🏗 High-Level Architecture

Controller → Service → Kafka Producer → Kafka Consumer → Execution Engine → Database

Order submission is decoupled from execution to avoid blocking request threads and improve scalability.

---

## 📊 Order Lifecycle

CREATED  
→ VALIDATED  
→ SENT_TO_EXECUTOR  
→ EXECUTING  
→ EXECUTED  
→ CANCELLED / FAILED  

---

## 📜 Event Timeline

Each order stores immutable events in `order_events` table:

- ORDER_PLACED
- SENT_TO_EXECUTOR
- EXECUTING
- EXECUTED
- CANCEL_REQUESTED
- CANCELLED
- FAILED

This enables full auditability and replay capability.

---

## 🔁 Idempotent Order Placement

`POST /api/v1/orders`

Supports idempotency using `Idempotency-Key` header.

For a given `(userId, Idempotency-Key)`:

- Only one order is created
- Duplicate retries return the original order
- Duplicate executions are prevented

Enforced via:
- Database-level unique constraint
- Service-layer duplicate detection

---

## 🚀 Async Execution (Kafka-Based)

Flow:

1. Order is persisted
2. Event published to Kafka topic `order-execution`
3. Kafka consumer processes message
4. Execution engine updates order status
5. Event recorded

This ensures:
- Non-blocking REST calls
- Scalable execution
- Decoupled services

---

## 🧠 Concurrency Handling

Optimistic locking (`@Version`) is used to prevent race conditions between:

- Kafka execution thread
- Cancel API thread

If a stale update occurs:
- `OptimisticLockingFailureException` is thrown
- Order state integrity is preserved

---

## 🧩 Strategy Pattern

Execution behavior is extensible via:

- `ExecutionStrategy` interface
- MarketExecutionStrategy
- LimitExecutionStrategy

Allows adding new order types without modifying core logic.

---

## 🐳 Deployment

The backend is fully Dockerized.

Docker Compose runs:

- Backend
- PostgreSQL
- Kafka
- Zookeeper

Environment variables are used for configuration.

---

## 📌 API Endpoints

POST `/api/v1/orders` → Place order  
GET `/api/v1/orders/{id}` → Get order details  
GET `/api/v1/orders` → Paginated orders  
POST `/api/v1/orders/{id}/cancel` → Cancel order  
GET `/api/v1/orders/{id}/events` → Fetch order timeline  

---

## 🔮 Future Improvements

- Dead Letter Queue (DLQ)
- Retry logic with backoff
- Outbox pattern
- Exactly-once semantics exploration
- Kubernetes deployment
