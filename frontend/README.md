# OrderFlow Frontend

React-based UI for interacting with the OrderFlow OMS backend.

Provides order placement, order tracking, event timeline visualization, and cancellation functionality.

---

## 🧱 Tech Stack

- React
- Axios
- React Router
- Functional Components + Hooks

---

## 🚀 Features

- Place new order
- View single order details
- View order event timeline
- Cancel order (when applicable)
- Paginated order list
- Dynamic status updates

---

## 📊 Order Timeline UI

Displays immutable order events:

- ORDER_PLACED
- SENT_TO_EXECUTOR
- EXECUTING
- EXECUTED
- CANCEL_REQUESTED
- CANCELLED
- FAILED

---

## 🛠 Running Locally

```bash
npm install
npm start
