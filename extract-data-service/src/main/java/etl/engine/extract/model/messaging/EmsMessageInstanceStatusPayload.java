package etl.engine.extract.model.messaging;

import etl.engine.extract.model.EtlExecutionInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@ToString
public class EmsMessageInstanceStatusPayload {

    private final UUID id;
    private final String type;
    private final String state;
    private final Collection<EtlExecutionInfo> workload;

}
