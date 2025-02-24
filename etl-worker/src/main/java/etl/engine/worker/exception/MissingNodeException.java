package etl.engine.worker.exception;

public class MissingNodeException extends Exception {

    public static MissingNodeException forPath(String path) {
        return new MissingNodeException(String.format("The node for the path '%s' was not found!", path));
    }

    public MissingNodeException() {
        super();
    }

    public MissingNodeException(String message) {
        super(message);
    }

    public MissingNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingNodeException(Throwable cause) {
        super(cause);
    }

    protected MissingNodeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
