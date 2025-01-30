# etl-engine

It's just a sandbox for my ideas after a lot of integration projects.

The main goal for the repo is not to provide a production-ready code but to proof the idea. 

## Services

- [ETL Management Service](etl-management-service/README.md)
- [Extract Data Service](extract-data-service/README.md)

## Command shortcuts

### How to build and run

```shell
$ mvn clean package && docker compose up --build
```

### How to consume messages from a topic manually

```shell
$ docker exec --workdir /opt/kafka/bin -it etl-kafka sh
$ ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic ems.heartbeat --from-beginning
```
