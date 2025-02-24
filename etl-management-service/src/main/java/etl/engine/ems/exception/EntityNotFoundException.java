package etl.engine.ems.exception;

import java.util.UUID;

public class EntityNotFoundException extends Exception {

    public static EntityNotFoundException forEtlExecution(UUID id) {
        return new EntityNotFoundException(String.format("The ETL-execution with id='%s' was not found!", id));
    }

    public static EntityNotFoundException forEtlExecutionStatus(String status) {
        return new EntityNotFoundException(String.format("The ETL-execution status '%s' was not found!", status));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    protected EntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
