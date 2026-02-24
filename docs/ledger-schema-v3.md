# Ledger Database Schema – Version 3

## Status
Fintech-Grade Final Design

## Objective
Enforce double-entry accounting principles with:
- Service-layer sign computation
- Idempotent request guarantees
- Optimistic locking for concurrency control
- Zero-sum transaction integrity
- Balance sheet reporting support
- Production-ready relational integrity

---

## Table: account

Represents a financial account stored in base currency.

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_name | VARCHAR(100) | NOT NULL |
| account_type | VARCHAR(20) | NOT NULL (ASSET / LIABILITY / EQUITY) |
| balance | DECIMAL(15,2) | NOT NULL |
| status | VARCHAR(20) | NOT NULL (ACTIVE / CLOSED) |
| version | BIGINT | NOT NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Design Rules
- `version` enables optimistic locking.
- Balance updated atomically within database transaction.
- No physical deletion allowed.
- Account types drive balance sheet grouping.

---

## Table: ledger_transaction

Each logical business transaction inserts **two rows**
(one debit entry and one credit entry).

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_id | BIGINT | NOT NULL, FK → account(id) |
| request_id | VARCHAR(100) | NOT NULL |
| source_amount | DECIMAL(15,2) | NOT NULL |
| currency_code | VARCHAR(10) | NOT NULL |
| exchange_rate_at_runtime | DECIMAL(18,6) | NOT NULL |
| signed_amount | DECIMAL(15,2) | NOT NULL |
| transaction_type | VARCHAR(10) | NOT NULL (DEBIT / CREDIT) |
| document_uri | VARCHAR(255) | NULL |
| created_by | VARCHAR(100) | NOT NULL |
| transaction_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Constraints

- FK ON DELETE RESTRICT
- CHECK (signed_amount != 0)
- UNIQUE (request_id, account_id, transaction_type)

---

## Double-Entry Enforcement (Service Layer Rule)

For every logical transaction:

1. Compute signed amounts based on account type and transaction type.
2. Insert exactly two rows (one positive, one negative).
3. Ensure sum(signed_amount) per request_id = 0.
4. Update account balances atomically.
5. Execute within a single database transaction.

---

## Index Recommendations

- INDEX(account_id)
- INDEX(transaction_date)
- INDEX(account_type)
- UNIQUE(request_id, account_id, transaction_type)

---

## Version Evolution
This version finalizes the transition to service-layer accounting logic,
simplifies SQL aggregation via signed_amount, and enforces concurrency-safe,
idempotent, zero-sum financial transactions suitable for production systems.