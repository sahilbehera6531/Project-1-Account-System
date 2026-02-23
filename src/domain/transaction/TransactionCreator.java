package domain.transaction;

import domain.Transaction;
import domain.TransactionType;
import java.math.BigDecimal;

public interface TransactionCreator {
    Transaction create(BigDecimal amount, TransactionType type, String accountNumber);
}
