package etl.engine.worker.exception;

public class InvalidEtlConfigurationException extends Exception {

    public static InvalidEtlConfigurationException forMissingNode(String path) {
        return new InvalidEtlConfigurationException(String.format("The node for the path '%s' is missing!", path));
    }

    public static InvalidEtlConfigurationException forEmptyNode(String path) {
        return new InvalidEtlConfigurationException(String.format("The node for the path '%s' is empty!", path));
    }

    public InvalidEtlConfigurationException() {
        super();
    }

    public InvalidEtlConfigurationException(String message) {
        super(message);
    }

    public InvalidEtlConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEtlConfigurationException(Throwable cause) {
        super(cause);
    }

    protected InvalidEtlConfigurationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
