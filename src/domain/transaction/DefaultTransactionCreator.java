package domain.transaction;

import domain.Transaction;
import domain.TransactionType;
import exception.BusinessException;
import util.TransactionIdGenerator;

public class DefaultTransactionCreator implements TransactionCreator {

    @Override
    public Transaction create(double amount, TransactionType type, String accountNumber) {
        try {
            return new Transaction(
                    TransactionIdGenerator.generate(),
                    amount,
                    type,
                    System.currentTimeMillis(),
                    accountNumber
            );
        } catch (Exception e) {
            throw new BusinessException(
                    "Transaction creation failed for account " + accountNumber,
                    e
            );
        }
    }
}
