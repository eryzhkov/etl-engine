# ETL-Worker Service

The ETL-Worker service is the only place where all ETL-phases are took place.

An ETL-process is started by sending a command by EMS via the special topic *ems.control*. A command has the assignee (the particular ETL-Worker instance). 
So, only one ETL-Worker instance will proceed with the command. An ETL-Worker can proceed with only one ETL-process at once.

It's expected the command contain the whole ETL-configuration to extract, transform and load data.

## The implemented functionality

### Report instance status

ETL-W should report a status to the EMS with the fixed rate. The status describe the instance itself (unique identifier,
type and state - idle or busy) and may have an information about the running ETL-process.

The status is published to the special heartbeat topic (see the application.yaml).

The status is used by EMS to monitor available instances and to decide which one is more suitable to run the next ETL-process.

The configuration parameters to manage reporting are:
- ews.status-reporting.enabled - enables/disables the status reporting,
- ews.status-reporting.initial-delay-ms - initial delay before status reporting starts,
- ews.status-reporting.fixed-rate-ms - the rate of the status reporting.

### Run the ETL-execution

ETL-W consumes commands from the *ems.control* topic and runs ETL-execution using information from the command.

Using the information, ETL-W creates ETL-extractor to extract data from the external system
pointed out the in the configuration, creates transformers and loaders to handle the extracted data.

During all these steps ETL-W notifies EMS about ETL-execution steps.