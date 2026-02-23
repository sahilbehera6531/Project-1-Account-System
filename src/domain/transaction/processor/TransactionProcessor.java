package domain.transaction.processor;

import domain.Account;
import domain.Transaction;

public interface TransactionProcessor {

    void process(Account account, Transaction transaction);
}
