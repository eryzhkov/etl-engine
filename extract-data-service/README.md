# Extract Data Service (EDS)

EDS is one of four ETL-services in the project. The main goal of one is to start an ETL-process by a command from the 
ETL Management Service (EMS). The command has enough information to let EDS to get the ETL-specification from the EMS 
via REST API. After that, EDS connects to an external data source, extract data and publish ones to the output topic as 
series of JSONs.

## The implemented functionality

### Report instance status

EDS should report a status to the EMS with the fixed rate. The status describe the instance itself (unique identifier, 
type and state - idle or busy) and may have an information about the running/scheduled ETL-process.

The status is published to the special heartbeat topic (see the application.yaml).

The status is used by EMS to monitor available instances and to decide which one is more suitable to run the next ETL-process.

The configuration parameters to manage reporting are:
- eds.status-reporting.enabled - enables/disables the status reporting,
- eds.status-reporting.initial-delay-ms - initial delay before status reporting starts,
- eds.status-reporting.fixed-rate-ms - the rate of the status reporting.

### Run the ETL-execution

EDS consumes commands from the *ems.control* topic and runs ETL-execution using information from the command.

Using the information, EDS requests the ETL-configurations and creates ETL-extractor to extract data from the external system 
pointed out the in the configuration.

During all these steps EDS notifies EMS about ETL-execution changes and extracting data from the external system.

Right now, there are no any fully implemented ETL-extractors. There is one that provides just an empty data.
