package etl.engine.extract.service.extractor.jdbc.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class Query {

    private final String sql;

}
