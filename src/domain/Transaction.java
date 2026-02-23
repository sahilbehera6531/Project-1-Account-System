package domain;

import java.math.BigDecimal;

public final class Transaction {

    private final String transactionId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final long timestamp;
    private final String accountNumber;

    public Transaction(String transactionId,
                       BigDecimal amount,
                       TransactionType type,
                       long timestamp,
                       String accountNumber) {

        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("Invalid transactionId");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (type == null) {
            throw new IllegalArgumentException("Transaction type required");
        }

        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Invalid account number");
        }

        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
        this.accountNumber = accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
