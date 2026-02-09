package exception;

public class BalanceViolationException extends BusinessException {

    public BalanceViolationException(String msg) {
        super(msg);
    }
}
