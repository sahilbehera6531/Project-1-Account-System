package domain.validation;

import exception.BalanceViolationException;
import exception.InvalidTransactionException;
import java.math.BigDecimal;

public final class AccountValidator {

    private AccountValidator() {}

    public static void validateDeposit(BigDecimal amount) {
        validateAmount(amount);
    }

    public static void validateWithdraw(BigDecimal amount, BigDecimal currentBalance) {

        validateAmount(amount);

        if (currentBalance == null) {
            throw new IllegalArgumentException("Current balance cannot be null");
        }

        if (currentBalance.compareTo(amount) < 0) {
            throw new BalanceViolationException("Insufficient balance");
        }
    }

    public static void validateAmount(BigDecimal amount) {

        if (amount == null) {
            throw new InvalidTransactionException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be positive");
        }
    }
}
