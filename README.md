# Project 1 — Ledger Account System
---

## 📌 Overview
Domain-driven ledger system built using:
- Java
- SOLID Principles
- Business Invariants
- Exception-driven validation

This project models a financial ledger domain ensuring transaction correctness and balance consistency.  
The goal was to design a backend-ready domain layer with strong invariants and extensible architecture.

---

## ✨ Features
- Account creation
- Deposit / Withdraw logic
- Transaction history tracking
- Invariant enforcement (state == derived state)
- Custom business exception handling
- Interface-based extensible transaction creation

---

## 🏗 Architecture

domain/
├── Account
├── Transaction
├── TransactionType
├── validation/
├── transaction/

exception/
├── BusinessException
├── BalanceViolationException
├── InvalidTransactionException

util/
├── TransactionIdGenerator


---

## 🧠 SOLID Principles Applied

### SRP — Single Responsibility Principle
Validation logic separated into AccountValidator.

### OCP — Open Closed Principle
Transaction creation handled via TransactionCreator interface.

### LSP — Liskov Substitution Principle
Different transaction creator implementations can be used interchangeably.

### ISP — Interface Segregation Principle
Focused interface for transaction creation.

### DIP — Dependency Inversion Principle
Account depends on TransactionCreator abstraction, not concrete class.

---

## 🧠 Design Decisions

### Why Domain-Driven Structure?
To separate business logic from infrastructure and make future Spring Boot migration easier.

### Why Business Invariant Enforcement?
Ensures account balance is always consistent with transaction history.

### Why Interface-based Transaction Creation?
To follow Open-Closed Principle and allow future transaction strategies without modifying Account class.

### Why Custom Exception Hierarchy?
To clearly separate business errors from system errors.

---

## ▶ How To Run

### Prerequisites
- Java 17 or above
- VS Code / IntelliJ

### Steps
1. Clone repository
2. Open project in IDE
3. Run Main.java

---

## 📚 Learning Outcomes
- Applied SOLID principles in real project
- Implemented domain invariants
- Designed exception hierarchy
- Implemented interface-based extensibility
- Practiced Git and GitHub professional workflow

---

## 🚀 Future Scope
- Spring Boot REST APIs
- Database integration (JPA / Hibernate)
- Ledger reporting APIs
- Authentication and security layer

