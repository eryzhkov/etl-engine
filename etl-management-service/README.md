# ETL Management Service (EMS)

The main goals of the EMS is to 
- manage ETL-process configurations,
- schedule ETL-process,
- monitor availability and status of ETL-services.

## Implemented functionality

### Monitor ETL-service instance status

EMS consumes statuses published by ETL-service instances to the heartbeat topic and saves them the corresponding table 
in a database. An instance can be in one of statuses: online, unknown, offline. If an instance stays in the offline status 
too long it is removed from the table.

The monitoring configuration is defined via parameters:
- ems.monitoring.initial-delay-ms - initial delay before monitoring starts
- ems.monitoring.fixed-rate-ms - the rate of the monitoring
- ems.monitoring.unknown-status-threshold-ms - the duration between the last status report time and now to assign status 'unknown' to the instance
- ems.monitoring.offline-status-threshold-ms - the duration between the last status report time and now to assign status 'offline' to the instance

The monitoring assigns statuses as follows:
- if duration <= fixed-rate-ms - the status stays 'online',
- if fixed-rate-m < duration <= unknown-status-threshold-ms - assign the 'unknown' status,
- if unknown-status-threshold-ms < duration <= offline-status-threshold-ms - assign the 'offline' status,
- if duration > offline-status-threshold-ms - the instance is removed from the table.

Please note, the ems.monitoring.fixed-rate-ms should be equal or more than eds.status-reporting.fixed-rate-ms (see [EDS](../extract-data-service/README.md))!

The actual instances are available via REST API endpoint:

```
GET /api/v1/monitoring/services
```

The monitoring results should be used by the scheduler to identify the available EDS to start an ETL-process.