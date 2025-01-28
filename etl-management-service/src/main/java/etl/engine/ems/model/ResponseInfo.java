package etl.engine.ems.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@ToString
public class ResponseInfo {
    private final String status;
    private final LocalDateTime timestamp;
    private final String description;
}
