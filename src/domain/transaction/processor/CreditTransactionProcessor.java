package domain.transaction.processor;

import domain.Account;
import domain.Transaction;

public class CreditTransactionProcessor implements TransactionProcessor {

    @Override
    public void process(Account account, Transaction transaction) {
        account.increaseBalance(transaction.getAmount());
    }
}
