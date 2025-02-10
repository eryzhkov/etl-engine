package etl.engine.extract.exception;

public class EtlConfigurationParseException extends Exception {

    public EtlConfigurationParseException() {
        super();
    }

    public EtlConfigurationParseException(String message) {
        super(message);
    }

    public EtlConfigurationParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtlConfigurationParseException(Throwable cause) {
        super(cause);
    }

    protected EtlConfigurationParseException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
