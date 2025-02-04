# ETL Management Service Topics Overview

## The ems.heartbeat topic

**Producer**: all ETL-service instances except ETL Management Service (EMS).

**Consumer**: EMS

**Message format**: JSON

The topic *ems.heartbeat* is used to notify EMS about the actual ETL-service instance status. 
Each ETL-service instance should report its status with the fixed rate.

The expected status message structure is:

```json
{
  "info": {
    "type": "notification",
    "signal": "service-status"
  },
  "payload": {
    "id": uuid,
    "type": "DATA_EXTRACTOR|STRUCTURE_TRANSFORMER|DATA_TRANSFORMER|DATA_LOADER",
    "state": "idle|busy",
    "workload": [],
    "timestamp": date-time
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

**Message format**: JSON

The topic is used to publish commands to start a new ETL-execution of the ETL-process.

The expected command structure is:

```json
{
  "info": {
    "action": "etl-start",
    "recipientInstanceId": uuid,
    "timestamp": date-time
  },
  "etlExecution": {
    "id": uuid,
    "scheduledAt": date-time,
    "acceptedAt": date-time,
    "startedAt": date-time,
    "finishedAt": date-time,
    "comment": string,
    "createdAt": date-time,
    "updatedAt": date-time,
    "etlProcess": {
      "id": uuid,
      "name": string,
      "code": string,
      "description": string,
      "createdAt": date-time,
      "updatedAt": date-time,
      "externalSystem": {
        "id": uuid,
        "name": string,
        "code": string,
        "description": string,
        "createdAt": date-time,
        "updatedAt": date-time
      }
    },
    "status": {
      "id": 1,
      "name": "SCHEDULED",
      "description": "The ETL-execution was published to the EMS management topic.",
      "createdAt": date-time,
      "updatedAt": date-time
    }
  }
}
```

where:

- *recipientInstanceId* is the unique identifier of the ETL-service instance (taken from the *etl_instances.instance_id* column).

The ETL-service instance should put the needed information from the command to the preparation queue of ETL executions.

The preparation queue handler should request the ETL-configuration for the ETL-process first. If succeeded, an ETL-execution 
job is created and enqueued to the ready-to-start queue. After that the handler should notify EMS via the *ems.progress* topic (see below) 
that the ETL-execution is accepted.

## The ems.progress topic

**Producer**: all ETL-service instances except ETL Management Service (EMS).

**Consumer**: EMS

**Message format**: JSON

The topic is used by ETL-services to notify EMS about ETL-process execution.

### The 'ETL-execution started' notification

```json
{
  "info": {
    "notification": "etl-execution-started",
    "timestamp": date-time
  },
  "etlExecution": {
    "id": uuid
  }
}
```
The notification is sent by EDS (ETL Data-Extractor Service).

### The 'ETL-execution finished' notification

```json
{
  "info": {
    "notification": "etl-execution-finished",
    "timestamp": date-time
  },
  "etlExecution": {
    "id": uuid
  }
}
```

The notification is sent by EDL (ETL Data-Loader Service).

### The 'ETL data stream started' notification

```json
{
  "info": {
    "notification": "etl-data-stream-started",
    "timestamp": date-time
  },
  "etlStreamExecution": {
    "etlInstance": uuid,
    "streamName": string,
    "etlExecution": {
      "id": uuid
    },
    "etlPhase": {
      "code": "data-extract|structure-transform|data-transform|data-load"
    }
  }
}
```

EMS should add a new row to the *etl_executions* table using the information from the notification.

### The 'ETL data stream finished' notification

```json
{
  "info": {
    "notification": "etl-data-stream-finished",
    "timestamp": date-time
  },
  "etlStreamExecution": {
    "etlInstance": uuid,
    "streamName": string,
    "etlExecution": {
      "id": uuid
    },
    "etlPhase": {
      "code": "data-extract|structure-transform|data-transform|data-load"
    }
  }
}
```

EMS should update a row in the *etl_executions* table using the information from the notification.