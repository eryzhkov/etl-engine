package etl.engine.extract.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class DataStreamInfo {

    private final String dataStreamName;

    private final JsonNode config;

    @Setter
    private long totalInMessages = -1L;

    @Setter
    private long totalOutMessages = -1L;

    @Setter
    private long totalFailedMessages = -1L;

    public void incrementTotalOutMessages() {
        this.totalOutMessages++;
    }

}
