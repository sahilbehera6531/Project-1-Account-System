package domain.transaction.processor;

import domain.Account;
import domain.Transaction;

public class DebitTransactionProcessor implements TransactionProcessor {

    @Override
    public void process(Account account, Transaction transaction) {
        account.decreaseBalance(transaction.getAmount());
    }
}
    