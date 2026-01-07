# OrderFlow

OrderFlow is a backend-focused Order Management and Trade Processing platform inspired by real-world trading systems.

It demonstrates asynchronous processing, clean architecture, and extensible design patterns commonly used in large-scale backend systems.

---

## Tech Stack
- Java 21
- Spring Boot
- PostgreSQL
- REST APIs
- ExecutorService (Asynchronous processing)

---

## High-Level Architecture
- Layered Spring Boot application (Controller → Service → Execution Engine)
- Order submission is decoupled from order execution to avoid blocking request threads
- Asynchronous execution engine processes orders in background worker threads
- Strategy pattern is used to support different order execution behaviors

---

## Core Features
- Place orders via REST APIs
- Asynchronous order execution using a dedicated thread pool
- Strategy-based execution (Market / Limit)
- Order lifecycle management (CREATED → EXECUTING → EXECUTED / FAILED)
- Structured error responses with error codes
- DTO-based API contracts (command vs query separation)

---

## Design Highlights
- Clear separation of concerns across layers
- Asynchronous execution to improve throughput and protect request threads
- Strategy pattern for extensibility without modifying core logic
- Command vs Query DTO separation for clean API contracts
- Centralized exception handling with structured error responses
- Concurrency handled using ExecutorService to isolate execution workloads

---

## API Overview
- `POST /orders` → Place a new order
- `GET /orders/{id}` → Fetch order details

---

## Future Enhancements
- Kafka-based event-driven execution
- Retry and failure handling with dead-letter queues
- Dockerized deployment
- Microservices decomposition
- Cloud deployment

These enhancements are planned to demonstrate system evolution from a monolith to a scalable, event-driven architecture.
