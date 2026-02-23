# Ledger Database Schema – Version 1

Status: Design Phase (Pre-MySQL Implementation)  
Date: February 2026  

---

## 1️⃣ Design Goals

- Prevent data redundancy
- Maintain referential integrity
- Ensure ACID compliance later
- Store balance as controlled denormalized field
- Never physically delete financial history

---

# TABLE: account

Represents a financial account.

### Columns

| Column Name   | Type             | Constraints                         |
|---------------|------------------|-------------------------------------|
| id            | BIGINT           | PRIMARY KEY, AUTO_INCREMENT         |
| account_name  | VARCHAR(100)     | NOT NULL                            |
| balance       | DECIMAL(15,2)    | NOT NULL                            |
| status        | VARCHAR(20)      | NOT NULL (ACTIVE / CLOSED)          |
| created_at    | TIMESTAMP        | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

### Notes

- `id` is a surrogate primary key.
- `balance` is a cached aggregate derived from transactions.
- Accounts must NOT be physically deleted.
- Use status = CLOSED instead of DELETE.
- Balance must be updated atomically with transaction insert.

---

# TABLE: ledger_transaction

Represents financial transactions.

### Columns

| Column Name       | Type           | Constraints                                 |
|-------------------|---------------|---------------------------------------------|
| id                | BIGINT        | PRIMARY KEY, AUTO_INCREMENT                 |
| account_id        | BIGINT        | NOT NULL, FOREIGN KEY → account(id)         |
| amount            | DECIMAL(15,2) | NOT NULL                                    |
| transaction_type  | VARCHAR(20)   | NOT NULL (DEBIT / CREDIT / etc.)            |
| transaction_date  | TIMESTAMP     | NOT NULL, DEFAULT CURRENT_TIMESTAMP         |

### Foreign Key Constraint

- `account_id` references `account(id)`
- ON DELETE RESTRICT
- ON UPDATE RESTRICT

### Notes

- `amount` must always be positive.
- `transaction_type` determines debit or credit.
- Transactions are the source of truth.
- No cascade delete allowed.

---

# Normalization Status

✔ 1NF – All fields atomic  
✔ 2NF – No partial dependency  
✔ 3NF – No transitive dependency  
⚠ Denormalization: `balance` stored intentionally for performance  

---

# Future Enhancements (Not Implemented Yet)

- Index on account_id
- Index on transaction_date
- Pagination strategy
- Audit logging
- Soft delete strategy
- Optimistic locking version column

---

End of Schema v1
