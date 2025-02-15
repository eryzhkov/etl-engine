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

### Provide CRUD-operations via REST API for entities management

EMS provides REST API endpoints under the /api/v1 path.

To see more open the http://localhost:8080/swagger-ui/index.html after all services are started using docker-compose.yaml.

### Provide the ability to run ETL-process on demand via REST API

In order to run an ETL-process be sure you've already created an External System and ETL-process entities using the REST API 
described above.

To run an ETL-process it is enough to create an ETL-execution entity (or do it via Swagger):

```shell
curl -X 'POST' \
  'http://localhost:8080/api/v1/etl-executions' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "etlProcessId": "e14b84c7-3567-409a-a005-3dfd5f4d4fe2"
}'
```

Be sure, you're using the actual etlProcessId value here!

After that you will see in EMS and EDS logs how these services interacts with each other via Apache Kafka topics.

Finally, you should see all data as follows:

```shell
 curl -X 'GET' \
  'http://localhost:8080/api/v1/external-systems' \
  -H 'accept: */*' | jq

{
  "info": {
    "status": "ok",
    "timestamp": "2025-02-12T13:25:28.743598804Z",
    "description": "Get all external systems"
  },
  "payload": [
    {
      "id": "b144ba6d-eb63-41ba-bfea-87489d3398cc",
      "name": "SEDO Database",
      "code": "sedo-db",
      "description": "PostgreSQL 17",
      "createdAt": "2025-02-12T13:24:58.6583Z",
      "updatedAt": "2025-02-12T13:24:58.6583Z"
    }
  ]
}
```

```shell
 curl -X 'GET' \
  'http://localhost:8080/api/v1/etl-processes' \
  -H 'accept: */*' | jq

{
  "info": {
    "status": "ok",
    "timestamp": "2025-02-12T13:28:03.276595667Z",
    "description": "Get all ETL-processes"
  },
  "payload": [
    {
      "createdAt": "2025-02-12T13:27:39.967139Z",
      "updatedAt": "2025-02-12T13:27:39.967139Z",
      "id": "e46da950-a28a-4cf3-ac1f-0d7bc381af86",
      "name": "ETL Test Process 01",
      "code": "etl-01",
      "description": "Load test data",
      "externalSystem": {
        "id": "b144ba6d-eb63-41ba-bfea-87489d3398cc",
        "name": "SEDO Database",
        "code": "sedo-db",
        "description": "PostgreSQL 17",
        "createdAt": "2025-02-12T13:24:58.6583Z",
        "updatedAt": "2025-02-12T13:24:58.6583Z"
      }
    }
  ]
}
```

```shell
curl -X 'GET' \
  'http://localhost:8080/api/v1/etl-executions' \
  -H 'accept: */*' | jq

{
  "info": {
    "status": "ok",
    "timestamp": "2025-02-12T13:30:41.328767421Z",
    "description": "Get all ETL-executions"
  },
  "payload": [
    {
      "id": "3529fa38-2166-48b4-a5e7-d218532f2fb8",
      "scheduledAt": "2025-02-12T13:29:56.125089Z",
      "acceptedAt": "2025-02-12T13:29:56.269797Z",
      "startedAt": "2025-02-12T13:29:56.277934Z",
      "finishedAt": "2025-02-12T13:29:56.300156Z",
      "etlProcess": {
        "createdAt": "2025-02-12T13:27:39.967139Z",
        "updatedAt": "2025-02-12T13:27:39.967139Z",
        "id": "e46da950-a28a-4cf3-ac1f-0d7bc381af86",
        "name": "ETL Test Process 01",
        "code": "etl-01",
        "description": "Load test data",
        "externalSystem": {
          "id": "b144ba6d-eb63-41ba-bfea-87489d3398cc",
          "name": "SEDO Database",
          "code": "sedo-db",
          "description": "PostgreSQL 17",
          "createdAt": "2025-02-12T13:24:58.6583Z",
          "updatedAt": "2025-02-12T13:24:58.6583Z"
        }
      },
      "status": {
        "createdAt": "2025-02-12T13:22:33.669567Z",
        "updatedAt": "2025-02-12T13:22:33.669567Z",
        "id": 4,
        "name": "FINISHED",
        "description": "The ETL-execution was finished by the ETL-service of the data-load type."
      },
      "createdAt": "2025-02-12T13:29:56.127185Z",
      "updatedAt": "2025-02-12T13:29:56.340204Z"
    }
  ]
}
```

```shell
curl -X 'GET' \
  'http://localhost:8080/api/v1/etl-streams-executions' \
  -H 'accept: */*' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  1648    0  1648    0     0   105k      0 --:--:-- --:--:-- --:--:--  107k
{
  "info": {
    "status": "ok",
    "timestamp": "2025-02-12T14:01:40.098864629Z",
    "description": "Get all ETL-streams executions"
  },
  "payload": [
    {
      "createdAt": "2025-02-12T13:29:56.313854Z",
      "updatedAt": "2025-02-12T13:29:56.332776Z",
      "id": "17c9f3e2-ebc2-4993-8297-d67779e85ed3",
      "serviceInstanceId": "7be07f0e-81b3-4245-be19-a836dff0c0cd",
      "streamName": "positions-stream-1",
      "streamStartedAt": "2025-02-12T13:29:56.284049Z",
      "streamFinishedAt": "2025-02-12T13:29:56.295163Z",
      "totalInMessages": 0,
      "totalOutMessages": 0,
      "totalFailedMessages": 0,
      "etlExecution": {
        "id": "3529fa38-2166-48b4-a5e7-d218532f2fb8",
        "scheduledAt": "2025-02-12T13:29:56.125089Z",
        "acceptedAt": "2025-02-12T13:29:56.269797Z",
        "startedAt": "2025-02-12T13:29:56.277934Z",
        "finishedAt": "2025-02-12T13:29:56.300156Z",
        "etlProcess": {
          "createdAt": "2025-02-12T13:27:39.967139Z",
          "updatedAt": "2025-02-12T13:27:39.967139Z",
          "id": "e46da950-a28a-4cf3-ac1f-0d7bc381af86",
          "name": "ETL Test Process 01",
          "code": "etl-01",
          "description": "Load test data",
          "externalSystem": {
            "id": "b144ba6d-eb63-41ba-bfea-87489d3398cc",
            "name": "SEDO Database",
            "code": "sedo-db",
            "description": "PostgreSQL 17",
            "createdAt": "2025-02-12T13:24:58.6583Z",
            "updatedAt": "2025-02-12T13:24:58.6583Z"
          }
        },
        "status": {
          "createdAt": "2025-02-12T13:22:33.669567Z",
          "updatedAt": "2025-02-12T13:22:33.669567Z",
          "id": 4,
          "name": "FINISHED",
          "description": "The ETL-execution was finished by the ETL-service of the data-load type."
        },
        "createdAt": "2025-02-12T13:29:56.127185Z",
        "updatedAt": "2025-02-12T13:29:56.340204Z"
      },
      "etlPhase": {
        "createdAt": "2025-02-12T13:22:33.637784Z",
        "updatedAt": "2025-02-12T13:22:33.637784Z",
        "id": 1,
        "name": "The extract data phase",
        "code": "data-extract",
        "description": null
      }
    }
  ]
}
```




