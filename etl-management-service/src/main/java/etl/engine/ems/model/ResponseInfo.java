package etl.engine.ems.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Getter
@ToString
public class ResponseInfo {
    private final String status;
    private final OffsetDateTime timestamp;
    private final String description;
}
