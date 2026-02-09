package app;

import domain.Account;
import domain.Transaction;


public class Main {
    public static void main(String[] args) {

        Account acc = new Account("123", "Sahil", 1000);

        acc.deposit(500);
        acc.withdraw(300);

        System.out.println("Balance (state)      : " + acc.getBalance());
        System.out.println("Balance (from txns)  : " + acc.calculateBalanceFromTransactions());

        System.out.println("\nTransactions:");
        for (Transaction t : acc.getTransactions()) {
            System.out.println(
                    t.getType() + " | " +
                    t.getAmount() + " | " +
                    t.getAccountNumber()
            );
        }
    }
}