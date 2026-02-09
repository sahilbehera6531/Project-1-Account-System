package domain.transaction;

import domain.Transaction;
import domain.TransactionType;

public interface TransactionCreator {
    Transaction create(double amount, TransactionType type, String accountNumber);
}
