# ETL Management Service Topics Overview

## The ems.heartbeat topic

**Producer**: all ETL-service instances except ETL Management Service (EMS).

**Consumer**: EMS.

**Message format**: JSON.

**Partitions**: 1

**Message retention time (ms)**: 180000 (180 sec) 

The topic *ems.heartbeat* is used to notify EMS about the actual ETL-service instance status. 
Each ETL-service instance should report its status with the fixed rate.

The expected status message structure is:

```json
{
  "info": {
    "notification": "instance-status",
    "timestamp": date-time
  },
  "payload": {
    "id": uuid,
    "type": "DATA_EXTRACTOR|STRUCTURE_TRANSFORMER|DATA_TRANSFORMER|DATA_LOADER",
    "state": "idle|busy",
    "workload": []
  }
}
```

where:

- *id* is the unique ETL-service instance identifier (the random UUID value generated during instance starting up).
- *type* is the instance type and should be one of the - DATA_EXTRACTOR, STRUCTURE_TRANSFORMER, DATA_TRANSFORMER, DATA_LOADER.
- *state* is the instance state and should be onde of the - idle, busy.
- *workload* is an array of the processing ETL data streams at the moment (not yet implemented!).
- *timestamp* is the date-date when the status was generated and published.

EMS updates the *etl_instances* table using the messages.

## The ems.control topic

**Producer**: EMS.

**Consumer**: all ETL-service instance of the DATA_EXTRACTOR type.

**Message format**: JSON.

**Partitions**: 1

**Message retention time (ms)**: 180000 (180 sec)

The topic is used to publish commands to start a new ETL-execution of the ETL-process.

The expected command structure is:

```json
{
  "info": {
    "command": "etl-execution-start",
    "recipientInstanceId": uuid,
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "externalSystemCode": string,
    "etlProcessCode": string
  }
}
```

where:

- *recipientInstanceId* is the unique identifier of the ETL-service instance of the 'DATA_EXTRACTOR' type (taken from the *etl_instances.instance_id* column).

The ETL-service instance should put the needed information from the command to the preparation queue of ETL executions.

The preparation queue handler requests the ETL-configuration for the ETL-process first. If succeeded, an ETL-execution 
job is created and enqueued to the ready-to-start queue. After that the handler should notify EMS via the *ems.progress* topic (see below) 
that the ETL-execution is accepted.

## The ems.progress topic

**Producer**: all ETL-service instances except ETL Management Service (EMS).

**Consumer**: EMS.

**Message format**: JSON.

**Partitions**: 1

**Message retention time (ms)**: 180000 (180 sec)

The topic is used by ETL-services to notify EMS about ETL-process execution.

### The 'ETL-execution accepted' notification

```json
{
  "info": {
    "notification": "etl-execution-accepted",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid
  }
}
```
The notification is sent by EDS (ETL Data-Extractor Service) when an ETL-execution is ready to be started.

EMS updates the *accepted_at* value in the *etl_executions* table using the information from the message.

### The 'ETL-execution started' notification

```json
{
  "info": {
    "notification": "etl-execution-started",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid
  }
}
```
The notification is sent by EDS (ETL Data-Extractor Service) when an ETL-execution is started.

EMS updates the *started_at* value in the *etl_executions* table using the information from the message.

### The 'ETL-execution finished' notification

```json
{
  "info": {
    "notification": "etl-execution-finished",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid
  }
}
```

The notification is sent by EDL (ETL Data-Loader Service).

EMS updates the *finished_at* value in the *etl_executions* table using the information from the message.

### The 'ETL data stream started' notification

```json
{
  "info": {
    "notification": "etl-data-stream-started",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "phase": "data-extract|structure-transform|data-transform|data-load",
    "dataStreamName": string,
    "instanceId": uuid
  }
}
```

EMS adds a new row to the *etl_phase_executions* table using the information from the notification.

### The 'ETL data stream finished' notification

```json
{
  "info": {
    "notification": "etl-data-stream-finished",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "phase": "data-extract|structure-transform|data-transform|data-load",
    "dataStreamName": string,
    "instanceId": uuid
  }
}
```

EMS updates the *stream_finished_at* value of the row in the *etl_phase_executions* table using the information from the notification.

## The ems.stats topic

**Producer**: all ETL-service instances except ETL Management Service (EMS).

**Consumer**: EMS.

**Message format**: JSON.

**Partitions**: 1

**Message retention time (ms)**: 180000 (180 sec)

The topic is used to collect statistics during an ETL-execution.

```json
{
  "info": {
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "phase": "data-extract|structure-transform|data-transform|data-load",
    "dataStreamName": string,
    "instanceId": uuid,
    "totalReadMessages": integer,
    "totalWrittenMessages": integer,
    "totalFailedMessages": integer
  }
}
```

The message should be sent when a processing of a data-stream is finished. Up to the moment all stats should be kept by ETL-service in memory.