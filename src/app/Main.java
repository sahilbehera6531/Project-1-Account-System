package app;

import domain.Account;
import domain.TransactionType;
import java.math.BigDecimal;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        Account acc = new Account("123", "Sahil", new BigDecimal("1000"));

        Random random = new Random();

        System.out.println("\n--- Random Stress Test Start ---");

        for (int i = 0; i < 100; i++) {

            TransactionType type =
                    random.nextBoolean() ? TransactionType.CREDIT : TransactionType.DEBIT;

            BigDecimal amount = BigDecimal.valueOf(1 + random.nextInt(1000));

            try {
                acc.applyTransaction(type, amount);
            } catch (Exception e) {
                System.out.println("Rejected Txn: " + e.getMessage());
            }
        }

        System.out.println("--- Stress Test End ---");

        System.out.println("\n--- Consistency Check ---");

        BigDecimal stateBalance = acc.getBalance();
        BigDecimal ledgerBalance = acc.calculateBalanceFromTransactions();

        System.out.println("State Balance  : " + stateBalance);
        System.out.println("Ledger Balance : " + ledgerBalance);

        if (stateBalance.compareTo(ledgerBalance) == 0) {
            System.out.println("CONSISTENT ✅");
        } else {
            System.out.println("INCONSISTENT ❌");
        }
    }
}
