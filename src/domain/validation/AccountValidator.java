package domain.validation;

import exception.BalanceViolationException;
import exception.InvalidTransactionException;

public class AccountValidator {

    public static void validateDeposit(double amount) {
        if (amount <= 0)
            throw new InvalidTransactionException("Deposit must be positive");
    }

    public static void validateWithdraw(double amount, double balance) {

        if (amount <= 0)
            throw new InvalidTransactionException("Withdraw must be positive");

        if (amount > balance)
            throw new BalanceViolationException("Insufficient balance");
    }
}
