package etl.engine.extract.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.InputStream;

/**
 * The class is the container for data extracted from the external source.
 */
@RequiredArgsConstructor
@Getter
@ToString
public class EtlStreamData {

    private final InputStream dataStream;
    private final String dataStreamName;
    private final long totalIn;
    private final long totalFailed;

}
