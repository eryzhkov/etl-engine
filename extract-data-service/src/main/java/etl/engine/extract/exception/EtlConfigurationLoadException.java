package etl.engine.extract.exception;

public class EtlConfigurationLoadException extends Exception {

    public EtlConfigurationLoadException() {
        super();
    }

    public EtlConfigurationLoadException(String message) {
        super(message);
    }

    public EtlConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtlConfigurationLoadException(Throwable cause) {
        super(cause);
    }

    protected EtlConfigurationLoadException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
