ARG BUILD_DIR=/opt/build

FROM eclipse-temurin:17 AS builder
ARG BUILD_DIR
WORKDIR $BUILD_DIR
COPY target/etl-worker-*.jar ./etl-worker.jar
RUN java -Djarmode=layertools -jar etl-worker.jar extract

FROM eclipse-temurin:17-jre
ARG BUILD_DIR
RUN groupadd ews-grp && useradd --gid ews-grp --shell /bin/bash --create-home ews
USER ews:ews-grp
WORKDIR /opt/ews

COPY --from=builder $BUILD_DIR/spring-boot-loader/ ./
COPY --from=builder $BUILD_DIR/dependencies/ ./
COPY --from=builder $BUILD_DIR/snapshot-dependencies/ ./
COPY --from=builder $BUILD_DIR/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]