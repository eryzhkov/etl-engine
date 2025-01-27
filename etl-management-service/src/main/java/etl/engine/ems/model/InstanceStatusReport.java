package etl.engine.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The class is used to deserialize the service status report from the message.
 * @see etl.engine.ems.service.monitoring.instance.ServicesMonitoringListener#listener(String)  
 */
@Getter
@Setter
@ToString
public class InstanceStatusReport {

    @JsonProperty("id")
    private UUID instanceId;
    @JsonProperty("type")
    private String instanceType;
    @JsonProperty("state")
    private String instanceState;
    @JsonProperty("timestamp")
    private LocalDateTime reportedAt;

}
