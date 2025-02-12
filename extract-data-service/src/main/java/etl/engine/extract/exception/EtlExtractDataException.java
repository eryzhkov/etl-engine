package etl.engine.extract.exception;

public class EtlExtractDataException extends Exception {

    public EtlExtractDataException() {
        super();
    }

    public EtlExtractDataException(String message) {
        super(message);
    }

    public EtlExtractDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtlExtractDataException(Throwable cause) {
        super(cause);
    }

    protected EtlExtractDataException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
