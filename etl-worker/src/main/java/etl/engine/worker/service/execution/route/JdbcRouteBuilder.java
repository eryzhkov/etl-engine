package etl.engine.worker.service.execution.route;

import java.util.UUID;

import etl.engine.worker.service.execution.processor.RowToJsonProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

public class JdbcRouteBuilder extends RouteBuilder {

    private final UUID etlExecutionId;
    private final String dataStreamName;
    private final String dataSourceName;

    public JdbcRouteBuilder(
            CamelContext context,
            UUID etlExecutionId,
            String dataStreamName,
            String dataSourceName) {
        super(context);
        this.etlExecutionId = etlExecutionId;
        this.dataStreamName = dataStreamName;
        this.dataSourceName = dataSourceName;
    }

    @Override
    public void configure() throws Exception {
        from("direct:" + dataStreamName + "-" + etlExecutionId)
                .routeId(dataStreamName + "-" + etlExecutionId)
                .to("jdbc:" + dataSourceName + "?outputType=StreamList")
                .split(body())
                .streaming()
                .process(new RowToJsonProcessor())
                .to("mock:end?log=true");
    }
}
