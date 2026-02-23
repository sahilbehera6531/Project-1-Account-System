package domain;

import domain.transaction.DefaultTransactionCreator;
import domain.transaction.TransactionCreator;
import domain.transaction.processor.*;
import domain.validation.AccountValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Account {

    private final String accountNumber;
    private String holderName;
    private BigDecimal balance;
    private final List<Transaction> transactions = new ArrayList<>();
    private final TransactionCreator transactionCreator = new DefaultTransactionCreator();
    private final Map<TransactionType, TransactionProcessor> processors =
            new EnumMap<>(TransactionType.class);

    public Account(String accNo, String name, BigDecimal bal) {

        if (accNo == null || accNo.isEmpty())
            throw new IllegalArgumentException("Invalid Account Number");

        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Invalid Name");

        if (bal == null || bal.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Negative Balance Not Allowed");

        this.accountNumber = accNo;
        this.holderName = name;
        this.balance = bal;

        if (bal.compareTo(BigDecimal.ZERO) > 0) {
            Transaction openingTxn = new Transaction(
                    util.TransactionIdGenerator.generate(),
                    bal,
                    TransactionType.CREDIT,
                    System.currentTimeMillis(),
                    accountNumber
            );
            transactions.add(openingTxn);
        }

        processors.put(TransactionType.CREDIT, new CreditTransactionProcessor());
        processors.put(TransactionType.DEBIT, new DebitTransactionProcessor());
    }

    public void deposit(BigDecimal amount) {

        AccountValidator.validateDeposit(amount);

        increaseBalance(amount);

        Transaction tx = createTransactionRecord(amount, TransactionType.CREDIT);

        transactions.add(tx);
        verifyInvariant();
    }

    public void withdraw(BigDecimal amount) {

        AccountValidator.validateWithdraw(amount, balance);

        decreaseBalance(amount);

        Transaction tx = createTransactionRecord(amount, TransactionType.DEBIT);

        transactions.add(tx);
        verifyInvariant();
    }

    public void applyTransaction(TransactionType type, BigDecimal amount) {

        AccountValidator.validateAmount(amount);

        Transaction tx = createTransactionRecord(amount, type);
        TransactionProcessor processor = processors.get(type);

        if (processor == null) {
            throw new IllegalStateException("No processor found for " + type);
        }

        processor.process(this, tx);
        transactions.add(tx);
        verifyInvariant();
    }

    public void increaseBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void decreaseBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public BigDecimal calculateBalanceFromTransactions() {

        BigDecimal bal = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.CREDIT) {
                bal = bal.add(t.getAmount());
            } else {
                bal = bal.subtract(t.getAmount());
            }
        }

        return bal;
    }

    private void verifyInvariant() {

        BigDecimal txBalance = calculateBalanceFromTransactions();

        if (txBalance.compareTo(balance) != 0) {
            throw new IllegalStateException(
                    "Invariant broken: balance mismatch with transaction history"
            );
        }
    }

    private Transaction createTransactionRecord(BigDecimal amount, TransactionType type) {
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
