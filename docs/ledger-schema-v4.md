# Ledger Database Schema – Version 4

## Status
Fintech-Grade General Ledger (5 Account Types)

## Objective
Extend the existing V3 design to support:

- Full General Ledger (Asset, Liability, Equity, Revenue, Expense)
- Balance Sheet generation
- Income Statement generation
- Hierarchical Chart of Accounts
- Zero-sum enforcement
- Idempotent request guarantees
- Optimistic locking
- Multi-currency correctness
- Production-ready relational integrity

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
- Balance stored in base currency.
- Balance updated atomically within database transaction.
- No physical deletion allowed.
- Account types drive both Balance Sheet and Income Statement grouping.

---

## Table: ledger_transaction

Each logical business transaction inserts **two rows**
(one debit entry and one credit entry).

| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PRIMARY KEY |
| account_id | BIGINT | NOT NULL, FK → account(id) |
| request_id | VARCHAR(100) | NOT NULL |
| source_amount | DECIMAL(18,2) | NOT NULL |
| currency_code | VARCHAR(10) | NOT NULL |
| exchange_rate_at_runtime | DECIMAL(18,6) | NOT NULL |
| signed_amount | DECIMAL(18,2) | NOT NULL |
| transaction_type | VARCHAR(10) | NOT NULL (DEBIT / CREDIT) |
| document_uri | VARCHAR(255) | NULL |
| created_by | VARCHAR(100) | NOT NULL |
| transaction_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Constraints

- FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE RESTRICT
- FOREIGN KEY (parent_account_id) REFERENCES account(id) ON DELETE RESTRICT
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

1. Compute `signed_amount` based on:
   - account_type
   - transaction_type (DEBIT / CREDIT)

2. Insert exactly two rows (one positive, one negative).

3. Ensure:

   SUM(signed_amount) GROUP BY request_id = 0

4. Update account balances atomically.

5. Execute within a single database transaction.

---

## Financial Statement Support

### Balance Sheet

Includes:

- ASSET
- LIABILITY
- EQUITY

Aggregation example:

SUM(balance) GROUP BY account_type

---

### Income Statement

Includes:

- REVENUE
- EXPENSE

Calculation:

Net Income = SUM(REVENUE) - SUM(EXPENSE)

Net income may optionally be transferred to retained earnings (EQUITY).

---

## Index Recommendations

- INDEX(account_id)
- INDEX(transaction_date)
- INDEX(account_type)
- INDEX(parent_account_id)
- UNIQUE(request_id, account_id, transaction_type)

---

## Version Evolution

V1 → Basic relational structure  
V2 → Multi-currency + audit + service-layer sign  
V3 → Double-entry + optimistic locking + idempotency  
V4 → Full 5-type General Ledger with Balance Sheet & Income Statement support  