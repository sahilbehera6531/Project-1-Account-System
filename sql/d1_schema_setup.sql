-- DAY 1 | SECTIONS 1, 2, & 3: THE FOUNDATION
CREATE SCHEMA IF NOT EXISTS `general_ledger` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;

USE `general_ledger`;

-- Section 1 & 2: Accounts with Audit Metadata built-in
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    parent_account_id BIGINT DEFAULT NULL,
    balance DECIMAL(19, 4) DEFAULT 0.0000,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Audit Metadata (Section 2)
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_account FOREIGN KEY (parent_account_id) REFERENCES accounts(id)
);

-- Section 1: Journal Entries
CREATE TABLE IF NOT EXISTS journal_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) NOT NULL, 
    account_id BIGINT NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(19, 4) NOT NULL, 
    entry_type ENUM('DEBIT', 'CREDIT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES accounts(id),
    INDEX idx_transaction_id (transaction_id)
);

-- Section 3: Idempotency Base
CREATE TABLE IF NOT EXISTS idempotency_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE, 
    response_code INT,
    response_body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Day 1 | Section 4 | Step 1: Create the Accounting Period Table
CREATE TABLE IF NOT EXISTS accounting_periods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period_name VARCHAR(20) NOT NULL, -- e.g., 'APR-2026'
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_closed BOOLEAN DEFAULT FALSE, -- The Master Lock
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Day 1 | Section 4 | Step 2: Initialize the current month
INSERT INTO accounting_periods (period_name, start_date, end_date, is_closed)
VALUES ('APR-2026', '2026-04-01', '2026-04-30', FALSE);

SELECT * FROM accounting_periods;

-- Day 1 | Section 5 | Step 1: Enforce Non-Zero Journal Entries
-- Why: In banking, a 0.00 transaction is a logical error. 
-- This constraint blocks any entry where amount is exactly zero.
ALTER TABLE journal_entries 
ADD CONSTRAINT chk_journal_amount_nonzero CHECK (amount <> 0);

-- Day 1 | Section 5 | Step 2: Enforce Valid Account Types
-- Why: We don't want someone typing 'Personal' or 'Random' as an account type.
-- This forces the type to be one of the 5 Pillars of Accounting.
ALTER TABLE accounts 
ADD CONSTRAINT chk_account_type_valid 
CHECK (account_type IN ('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE'));

DESCRIBE accounts;