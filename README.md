# Project 1 — Ledger Account System

## Overview
Domain-driven ledger system built using:
- Java
- SOLID Principles
- Business Invariants
- Exception-driven validation

## Features
- Account creation
- Deposit / Withdraw logic
- Transaction history tracking
- Invariant enforcement (state == derived state)
- Custom business exception handling

## Architecture
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

## SOLID Applied
- SRP → Validation separated
- OCP → Transaction creation via interface
- LSP → TransactionCreator interchangeable
- ISP → Focused interfaces
- DIP → Account depends on abstraction

## Future Scope
- Spring Boot REST APIs
- Database integration (JPA / Hibernate)
- Ledger reporting APIs
