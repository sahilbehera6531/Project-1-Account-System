package domain;

import domain.transaction.DefaultTransactionCreator;
import domain.transaction.TransactionCreator;
import domain.validation.AccountValidator;
import java.util.ArrayList;
import java.util.List;
import util.TransactionIdGenerator;

public class Account {

    
    private final String accountNumber;
    private String holderName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();
    private final TransactionCreator transactionCreator = new DefaultTransactionCreator();


    public Account(String accNo, String name, double bal) {

        if (accNo == null || accNo.isEmpty())
            throw new IllegalArgumentException("Invalid Account Number");

        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Invalid Name");

        if (bal < 0)
            throw new IllegalArgumentException("Negative Balance Not Allowed");

        this.accountNumber = accNo;
        this.holderName = name;
        this.balance = bal;

        if (bal > 0) {
            Transaction openingTxn = new Transaction(
                    TransactionIdGenerator.generate(),
                    bal,
                    TransactionType.CREDIT,
                    System.currentTimeMillis(),
                    accountNumber
            );
            transactions.add(openingTxn);
        }
    }

    public void deposit(double amount) {

        AccountValidator.validateDeposit(amount);

        balance += amount;

        Transaction tx = createTransaction(amount, TransactionType.CREDIT);

        transactions.add(tx);
        verifyInvariant();
    }

    public void withdraw(double amount) {

        AccountValidator.validateWithdraw(amount, balance);

        balance -= amount;

        Transaction tx = createTransaction(amount, TransactionType.DEBIT);

        transactions.add(tx);
        verifyInvariant();
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public double calculateBalanceFromTransactions() {

        double bal = 0;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.CREDIT) {
                bal += t.getAmount();
            } else {
                bal -= t.getAmount();
            }
        }

        return bal;
    }

    private void verifyInvariant() {

        double txBalance = calculateBalanceFromTransactions();

        if (Double.compare(txBalance, balance) != 0) {
            throw new IllegalStateException(
                    "Invariant broken: balance mismatch with transaction history"
            );
        }
    }

    private Transaction createTransaction(double amount, TransactionType type) {
        return transactionCreator.create(amount, type, accountNumber);
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}
