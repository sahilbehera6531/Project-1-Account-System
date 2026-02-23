# Ledger Database Schema – Version 3

## Status
Fintech-Grade Final Design

## Objective
Enforce double-entry accounting, idempotent request handling,
optimistic locking for concurrency control, and zero-sum integrity.

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

### Design Notes
- `version` enables optimistic locking.
- Balance updated atomically within DB transaction.
- No physical deletion allowed.

---

## Table: ledger_transaction

Each logical business transaction inserts **two rows**
(one DEBIT and one CREDIT) to enforce double-entry principles.

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_id | BIGINT | NOT NULL, FK → account(id) |
| request_id | VARCHAR(100) | NOT NULL |
| source_amount | DECIMAL(15,2) | NOT NULL |
| currency_code | VARCHAR(10) | NOT NULL |
| exchange_rate_at_runtime | DECIMAL(18,6) | NOT NULL |
| base_amount | DECIMAL(15,2) | NOT NULL |
| transaction_type | VARCHAR(10) | NOT NULL (DEBIT / CREDIT) |
| document_uri | VARCHAR(255) | NULL |
| created_by | VARCHAR(100) | NOT NULL |
| transaction_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Constraints

- FK ON DELETE RESTRICT
- CHECK (source_amount > 0)
- CHECK (base_amount > 0)
- UNIQUE (request_id, account_id, transaction_type)

---

## Index Recommendations

- INDEX(account_id)
- INDEX(transaction_date)
- INDEX(account_id, transaction_date)
- INDEX(account_type)

---

## Double-Entry Enforcement Rule

For every logical transaction:

1. Insert one DEBIT row.
2. Insert one CREDIT row.
3. Ensure total debit equals total credit.
4. Update account balances atomically.
5. Execute within a single DB transaction.

---

## Version Evolution
This version enforces true double-entry modeling,
composite idempotency protection, optimistic locking,
and concurrency-safe zero-sum ledger updates suitable
for production-grade financial systems.