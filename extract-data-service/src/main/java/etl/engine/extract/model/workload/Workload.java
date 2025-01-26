package etl.engine.extract.model.workload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The class defines an ETL-job executing at the moment.
 * It's used for the reporting functionality.
 */
@RequiredArgsConstructor
@Getter
@ToString
public class Workload {

    private final String system;
    private final String process;
    private final String stream;

}
