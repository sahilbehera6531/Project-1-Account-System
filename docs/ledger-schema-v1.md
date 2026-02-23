# Ledger Database Schema – Version 1

## Status
Initial Relational Design

## Objective
Establish normalized ledger structure with referential integrity.

---

## Table: account

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_name | VARCHAR(100) | NOT NULL |
| balance | DECIMAL(15,2) | NOT NULL |
| status | VARCHAR(20) | NOT NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Table: ledger_transaction

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_id | BIGINT | FK → account(id) |
| amount | DECIMAL(15,2) | NOT NULL |
| transaction_type | VARCHAR(20) | NOT NULL |
| transaction_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Design Notes
- 3NF normalized
- Balance cached for performance
- No physical delete

## Version Evolution
This version establishes core relational structure without idempotency, concurrency control, or multi-currency support.