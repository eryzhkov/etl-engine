package etl.engine.ems.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ExternalSystemRequestDto {

    private String name;
    private String code;
    private String description;

}
