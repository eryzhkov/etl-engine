package etl.engine.worker.exception;

public class InvalidNodeValueException extends Exception {

    public InvalidNodeValueException() {
        super();
    }

    public InvalidNodeValueException(String message) {
        super(message);
    }

    public InvalidNodeValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNodeValueException(Throwable cause) {
        super(cause);
    }

    protected InvalidNodeValueException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
