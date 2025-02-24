package etl.engine.worker.exception;

public class InvalidNodeTypeException extends Exception {

    public static InvalidNodeTypeException forPathAndType(String path, String typeDescription) {
        return new InvalidNodeTypeException(String.format("The node for the path '%s' is not a '%s' type!", path, typeDescription));
    }

    public InvalidNodeTypeException() {
        super();
    }

    public InvalidNodeTypeException(String message) {
        super(message);
    }

    public InvalidNodeTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNodeTypeException(Throwable cause) {
        super(cause);
    }

    protected InvalidNodeTypeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
