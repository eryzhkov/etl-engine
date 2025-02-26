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
    "type": "heartbeat",
    "timestamp": date-time
  },
  "payload": {
    "id": uuid,
    "state": "idle|busy"
  }
}
```

where:

- *id* is the unique ETL-Worker instance identifier (the random UUID value generated during instance starting up).
- *state* is the instance state and should be one of the - idle or busy.
- *timestamp* is the date-date when the status was generated and published.

EMS updates the *etl_instances* table using the messages.

## The ems.control topic

**Producer**: EMS.

**Consumer**: all ETL-Worker instances.

**Message format**: JSON.

**Partitions**: 1

**Message retention time (ms)**: 180000 (180 sec)

The topic is used to publish commands to start a new ETL-execution of the ETL-process.

The expected command structure is:

```json
{
  "info": {
    "type": "etl-execution-assign",
    "assignee": uuid,
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "configuration": {...}
  }
}
```

where:

- *assignee* is the unique identifier of the ETL-Worker instance (taken from the *etl_instances.instance_id* column).
- *etlExecutionId* is the unique identifier of the ETL-execution created by EMS.
- *configuration* is the ETL-configuration for the ETL-execution.

EMS updates the *assigned_at* value in the *etl_executions* table before the command is sent.

## The ems.progress topic

**Producer**: all ETL-Worker instances.

**Consumer**: EMS.

**Message format**: JSON.

**Partitions**: 1

**Message retention time (ms)**: 180000 (180 sec)

The topic is used by ETL-Worker services to notify EMS about ETL-process execution.

### The 'ETL-execution accepted' message

```json
{
  "info": {
    "type": "etl-execution-accepted",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "assignee": uuid
  }
}
```
The message is sent by ETL-Worker when the assigned ETL-execution can be started on the instance.

EMS updates the *accepted_at* value in the *etl_executions* table using the information from the message.

### The 'ETL-execution rejected' message

```json
{
  "info": {
    "type": "etl-execution-rejected",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "assignee": uuid
  }
}
```

The message is sent by the ETL-Worker if it already has an assignment and is still in processing.

### The 'ETL-execution started' message

```json
{
  "info": {
    "type": "etl-execution-started",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "assignee": uuid
  }
}
```
The message is sent by ETL-Worker when an ETL-execution is started.

EMS updates the *started_at* value in the *etl_executions* table using the information from the message.

### The 'ETL-execution finished' message

```json
{
  "info": {
    "type": "etl-execution-finished",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "assignee": uuid
  }
}
```

The message is sent by ETL-Worker service when ETL-execution is finished.

EMS updates the *finished_at* value in the *etl_executions* table using the information from the message.

### The 'ETL-execution failed' message

```json
{
  "info": {
    "type": "etl-execution-failed",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "assignee": uuid,
    "reason": string
  }
}
```

The message is sent by ETL-Worker service when ETL-execution is failed for some reason.

EMS updates the *finished_at* and *comment* values in the *etl_executions* table using the information from the message.

### The 'ETL data stream started' message

```json
{
  "info": {
    "type": "etl-data-stream-started",
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
    "type": "etl-data-stream-finished",
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

### The 'ETL data stream stats' notification

```json
{
  "info": {
    "type": "etl-data-stream-stats",
    "timestamp": date-time
  },
  "payload": {
    "etlExecutionId": uuid,
    "phase": "data-extract|structure-transform|data-transform|data-load",
    "dataStreamName": string,
    "instanceId": uuid,
    "totalInMessages": integer,
    "totalOutMessages": integer,
    "totalFailedMessages": integer
  }
}
```

The message should be sent right before a processing of a data-stream is finished. Up to the moment all stats should be kept by ETL-service in memory.