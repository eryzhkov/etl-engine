package etl.engine.worker.service.execution;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import etl.engine.worker.exception.InvalidEtlConfigurationException;
import etl.engine.worker.exception.InvalidNodeTypeException;
import etl.engine.worker.exception.MissingNodeException;
import etl.engine.worker.model.EtlExecutionInfo;
import etl.engine.worker.service.execution.route.JdbcRouteBuilder;
import etl.engine.worker.service.instance.InstanceInfoManager;
import etl.engine.worker.service.messaging.MessagingService;
import etl.engine.worker.util.JsonUtils;
import etl.engine.worker.validator.EtlConfigurationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EtlExecutionManager {

    private final MessagingService messagingService;
    private final InstanceInfoManager instanceInfoManager;
    private final CamelContext camelContext;

    @Async("etlThreadAsyncPool")
    public void runEtlExecutionAsync(UUID etlExecutionId, OffsetDateTime lastRunAt, JsonNode configuration) {
        log.info("ENTER");
        log.debug("IN: etlExecutionId='{}', lastRunAt='{}', configuration={}", etlExecutionId, lastRunAt, configuration);
        messagingService.startEtlExecution(etlExecutionId);
        instanceInfoManager.takeWorkload(
                EtlExecutionInfo.builder()
                        .etlExecutionId(etlExecutionId)
                        .startedAt(OffsetDateTime.now())
                        .build()
        );
        log.info("The ETL-execution {} is taken.", etlExecutionId);
        HikariDataSource externalDataSource = null;
        final String externalDataSourceKey = "ext-ds";
        List<String> dataStreamsNames = new ArrayList<>();
        try {
            EtlConfigurationValidator.validate(configuration);

            // Create and bind the external data source.
            externalDataSource = prepareExternalDataSource(JsonUtils.getNode(configuration, "/external-datasource"));
            log.debug("The external data source is prepared.");
            camelContext.getRegistry().bind(externalDataSourceKey, externalDataSource);
            log.debug("The external data source is bound to the Camel Context.");

            // Find data streams.
            JsonNode streams = JsonUtils.getNode(configuration, "/streams");
            Iterator<String> iterator = streams.fieldNames();
            while(iterator.hasNext()) {
                String element = iterator.next();
                log.info("Found data stream: '{}'", element);
                dataStreamsNames.add(element);
            }
            // Create and register routes for all found data streams.
            for (String streamName : dataStreamsNames) {
                camelContext.addRoutes(
                        new JdbcRouteBuilder(camelContext, etlExecutionId, streamName, externalDataSourceKey));
                log.info("The route '{}-{}' is registered.", streamName, etlExecutionId);
            }
            // Start routes.
            for (String streamName : dataStreamsNames) {
                final String routeId = streamName + "-" + etlExecutionId;
                String sql = JsonUtils.readValueAsString(streams, "/" + streamName + "/extract/query/text");
                Endpoint endpoint = camelContext.getEndpoint("direct:" + routeId);
                Exchange exchange = endpoint.createExchange();
                exchange.getMessage().setBody(sql);
                log.info("The SQL query for the data stream '{}' is '{}'.", streamName, sql);
                try (ProducerTemplate template = exchange.getContext().createProducerTemplate()) {
                    //TODO The routes are executed in the current thread! Use executor pool?
                    Exchange out = template.send(endpoint, exchange);
                    log.info("Out: {}", out);
                    log.info("The route '{}' was started...", routeId);
                }
            }
            log.info("Sleep for 10 sec...");
            Thread.sleep(10000);
            log.info("Wake up!");
            messagingService.finishEtlExecution(etlExecutionId);
        } catch (InvalidEtlConfigurationException | MissingNodeException | InvalidNodeTypeException e) {
            log.error("The execution is failed: {}.", e.getMessage());
            messagingService.failEtlExecution(etlExecutionId, e.getMessage());
        } catch (Exception e) {
            log.error("The execution is failed for unexpected reason: {}.", e.getMessage());
            messagingService.failEtlExecution(etlExecutionId, e.getMessage());
        } finally {
            if (externalDataSource != null) {
                externalDataSource.close();
                for (String streamName : dataStreamsNames) {
                    final String routeId = streamName + "-" + etlExecutionId;
                    try {
                        camelContext.removeRoute(routeId);
                        log.info("The route '{}' was removed from the Camel Context.", routeId);
                    } catch (Exception e) {
                        log.warn("Failure to remove the route '{}' from the Camel Context: {}", routeId, e.getMessage());
                    }
                }
                camelContext.getRegistry().unbind(externalDataSourceKey);
                log.debug("The external data source is unbound from the Camel Context.");
            }
            instanceInfoManager.dropWorkload();
            log.info("The ETL-execution {} is dropped.", etlExecutionId);
        }
        log.info("EXIT");
    }

    protected HikariDataSource prepareExternalDataSource(JsonNode dataSourceConfig)
            throws MissingNodeException, InvalidNodeTypeException {
        String dataBaseType = JsonUtils.readValueAsString(dataSourceConfig, "/config/databaseType");
        String host = JsonUtils.readValueAsString(dataSourceConfig, "/config/host");
        int port = JsonUtils.readValueAsInt(dataSourceConfig, "/config/port");
        String database = JsonUtils.readValueAsString(dataSourceConfig, "/config/database");
        String user = JsonUtils.readValueAsString(dataSourceConfig, "/config/user");
        String password = JsonUtils.readValueAsString(dataSourceConfig, "/config/password");
        String jdbcUrl = "jdbc:" + dataBaseType + "://" + host + ":" + port + "/" + database;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        //TODO The pool size should correlate with the number of the working threads!
        config.addDataSourceProperty("maximumPoolSize", 1);
        config.addDataSourceProperty("minimumIdle", 1);
        return new HikariDataSource(config);
    }

}
