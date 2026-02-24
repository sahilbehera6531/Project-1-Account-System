# Ledger Database Schema – Version 4

## Status
Fintech-Grade General Ledger (5 Account Types)  
With Accounting Period & Reversal Support

## Objective
Support:

- Asset, Liability, Equity, Revenue, Expense
- Double-entry accounting
- Balance Sheet
- Income Statement
- Trial Balance
- Accounting Period closing
- Reversal entries
- Idempotent request guarantees
- Optimistic locking
- Multi-currency correctness

---

## Table: accounting_period

Represents a financial reporting period.

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| period_code | VARCHAR(7) | NOT NULL, UNIQUE (e.g., 2026-03) |
| start_date | DATE | NOT NULL |
| end_date | DATE | NOT NULL |
| is_closed | BOOLEAN | NOT NULL DEFAULT FALSE |

### Design Rules

- No journal entries allowed if `is_closed = TRUE`.
- Period must be created before posting transactions.
- Period closing is irreversible in this system.

---

## Table: account

Represents a financial account stored in base currency.

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_name | VARCHAR(100) | NOT NULL |
| account_type | VARCHAR(20) | NOT NULL (ASSET / LIABILITY / EQUITY / REVENUE / EXPENSE) |
| parent_account_id | BIGINT | NULL, FK → account(id) |
| balance | DECIMAL(18,2) | NOT NULL |
| status | VARCHAR(20) | NOT NULL (ACTIVE / CLOSED) |
| version | BIGINT | NOT NULL |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Design Rules

- `version` enables optimistic locking.
- `parent_account_id` enables hierarchical chart of accounts.
- No physical deletion allowed.
- Balance updated atomically within transaction.

---

## Table: ledger_transaction

Each logical business transaction inserts **two rows**
(one debit entry and one credit entry).

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_id | BIGINT | NOT NULL, FK → account(id) |
| request_id | VARCHAR(100) | NOT NULL |
| reversal_of_request_id | VARCHAR(100) | NULL |
| source_amount | DECIMAL(18,2) | NOT NULL |
| currency_code | VARCHAR(10) | NOT NULL |
| exchange_rate_at_runtime | DECIMAL(18,6) | NOT NULL |
| signed_amount | DECIMAL(18,2) | NOT NULL |
| transaction_type | VARCHAR(10) | NOT NULL (DEBIT / CREDIT) |
| posting_date | DATE | NOT NULL |
| accounting_period_code | VARCHAR(7) | NOT NULL, FK → accounting_period(period_code) |
| document_uri | VARCHAR(255) | NULL |
| created_by | VARCHAR(100) | NOT NULL |
| transaction_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Constraints

- FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE RESTRICT
- FOREIGN KEY (parent_account_id) REFERENCES account(id) ON DELETE RESTRICT
- FOREIGN KEY (accounting_period_code) REFERENCES accounting_period(period_code)
- CHECK (signed_amount != 0)
- UNIQUE (request_id, account_id, transaction_type)

---

## Account Type Sign Logic

| Account Type | Debit Effect | Credit Effect |
|--------------|-------------|--------------|
| ASSET        | +           | -            |
| LIABILITY    | -           | +            |
| EQUITY       | -           | +            |
| REVENUE      | -           | +            |
| EXPENSE      | +           | -            |

Sign is computed in service layer before persistence.

---

## Double-Entry Enforcement (Service Layer Rule)

For every logical transaction:

1. Validate accounting period is not closed.
2. Compute signed_amount based on account_type and transaction_type.
3. Insert exactly two rows (one positive, one negative).
4. Ensure SUM(signed_amount) per request_id = 0.
5. Update account balances atomically.
6. Execute within a single database transaction.

---

## Reversal Entry Rule

- Transactions cannot be deleted.
- Incorrect entries must be reversed.
- Reversal entry must reference `reversal_of_request_id`.
- Reversal must mirror original signed_amount values.

---

## Financial Statement Support

### Trial Balance

List all accounts and balances.
Ensure total debits = total credits.

### Balance Sheet

Includes:

- ASSET
- LIABILITY
- EQUITY

### Income Statement

Includes:

- REVENUE
- EXPENSE

Net Income = SUM(REVENUE) - SUM(EXPENSE)

---

## Index Recommendations

- INDEX(account_id)
- INDEX(account_type)
- INDEX(parent_account_id)
- INDEX(accounting_period_code)
- INDEX(posting_date)
- UNIQUE(request_id, account_id, transaction_type)

---

## Version Evolution

V1 → Basic relational structure  
V2 → Multi-currency + audit  
V3 → Double-entry + idempotency + optimistic locking  
V4 → 5-type General Ledger + Financial Statements + Period Control + Reversal Support  