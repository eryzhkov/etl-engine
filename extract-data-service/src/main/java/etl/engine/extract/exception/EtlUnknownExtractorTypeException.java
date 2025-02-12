package etl.engine.extract.exception;

public class EtlUnknownExtractorTypeException extends Exception {

    public EtlUnknownExtractorTypeException() {
        super();
    }

    public EtlUnknownExtractorTypeException(String message) {
        super(message);
    }

    public EtlUnknownExtractorTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtlUnknownExtractorTypeException(Throwable cause) {
        super(cause);
    }

    protected EtlUnknownExtractorTypeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
