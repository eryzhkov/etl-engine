ARG BUILD_DIR=/opt/build

FROM eclipse-temurin:17 AS builder
ARG BUILD_DIR
WORKDIR $BUILD_DIR
COPY target/extract-data-service-*.jar ./extract-data-service.jar
RUN java -Djarmode=layertools -jar extract-data-service.jar extract

FROM eclipse-temurin:17-jre
ARG BUILD_DIR
RUN groupadd eds-grp && useradd --gid eds-grp --shell /bin/bash --create-home eds
USER eds:eds-grp
WORKDIR /opt/ems

COPY --from=builder $BUILD_DIR/spring-boot-loader/ ./
COPY --from=builder $BUILD_DIR/dependencies/ ./
COPY --from=builder $BUILD_DIR/snapshot-dependencies/ ./
COPY --from=builder $BUILD_DIR/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]