package exception;

public class InvalidTransactionException extends BusinessException {

    public InvalidTransactionException(String msg) {
        super(msg);
    }
}
