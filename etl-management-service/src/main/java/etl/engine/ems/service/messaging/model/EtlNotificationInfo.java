package etl.engine.ems.service.messaging.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@ToString
@Getter
public class EtlNotificationInfo {

    private final String type;
    private final OffsetDateTime timestamp = OffsetDateTime.now();

}
