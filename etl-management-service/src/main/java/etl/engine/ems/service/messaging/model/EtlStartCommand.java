package etl.engine.ems.service.messaging.model;

import etl.engine.ems.model.EtlExecutionDto;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class EtlStartCommand {

    private final CommandInfo info;
    private final EtlExecutionDto etlExecution;

    public EtlStartCommand(UUID recipientInstanceId, EtlExecutionDto etlExecution) {
        this.info = new CommandInfo("etl-start", recipientInstanceId);
        this.etlExecution = etlExecution;
    }

}
