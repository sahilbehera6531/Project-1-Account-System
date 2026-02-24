# Ledger Database Schema – Version 2

## Status
Integrity & Multi-Currency Upgrade (Service-Layer Sign Transition)

## Objective
Enhance the initial ledger design to support:
- Idempotent request handling
- Multi-currency historical correctness
- Audit traceability
- Optimistic locking preparation
- Transition of accounting sign logic to service layer

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
- `account_type` introduced for balance sheet grouping.
- `version` added for optimistic locking support.
- Balance stored in base currency.
- No physical deletion allowed.

---

## Table: ledger_transaction

Represents immutable ledger entries.

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

### Design Notes
- `signed_amount` introduced.
- Sign is computed in service layer before persistence.
- Multi-currency conversion performed at runtime and stored.
- `transaction_type` retained for audit clarity.

---

## Version Evolution
This version introduces multi-currency handling, idempotency groundwork,
audit traceability, and transitions accounting sign logic from SQL to the
application service layer.