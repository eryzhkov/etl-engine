package etl.engine.ems.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ExternalSystemRequestDto {

    private UUID id;
    private String name;
    private String code;
    private String description;

}
