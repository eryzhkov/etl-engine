package etl.engine.ems.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EtlProcessRequestDto {

    private String name;
    private String code;
    private String description;
    private ExternalSystemRequestDto externalSystem;

}
