package etl.engine.ems.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import etl.engine.ems.service.monitoring.instance.HeartbeatTopicListener;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * The class is used to deserialize the service status report from the message.
 * @see HeartbeatTopicListener#listener(String)
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceStatusReport {

    @JsonProperty("id")
    private UUID instanceId;
    @JsonProperty("state")
    private String instanceState;
    @JsonProperty("timestamp")
    private OffsetDateTime reportedAt;

}
