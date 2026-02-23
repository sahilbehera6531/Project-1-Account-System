# Ledger Database Schema – Version 2

## Status
Integrity & Compliance Upgrade

## Objective
Enhance the initial ledger design to support idempotency, multi-currency correctness,
audit traceability, and optimistic locking preparation.

---

## Table: account

Represents a financial account.

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
- `account_type` added to support balance sheet grouping.
- `version` introduced to support optimistic locking.
- Balance stored in base currency.

---

## Table: ledger_transaction

Represents financial transactions.

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

### Design Notes
- Multi-currency support introduced.
- Historical exchange rate stored permanently.
- Audit reference (`document_uri`) added.
- Idempotency groundwork via `request_id`.

---

## Version Evolution
This version introduced idempotency support, audit traceability,
multi-currency correctness, and optimistic locking preparation
while retaining single-row transaction modeling.