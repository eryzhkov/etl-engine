ARG BUILD_DIR=/opt/build

FROM eclipse-temurin:17 AS builder
ARG BUILD_DIR
WORKDIR $BUILD_DIR
COPY target/etl-management-service-*.jar ./etl-management-service.jar
RUN java -Djarmode=layertools -jar etl-management-service.jar extract

FROM eclipse-temurin:17-jre
ARG BUILD_DIR
RUN groupadd ems-grp && useradd --gid ems-grp --shell /bin/bash --create-home ems
USER ems:ems-grp
WORKDIR /opt/ems

COPY --from=builder $BUILD_DIR/spring-boot-loader/ ./
COPY --from=builder $BUILD_DIR/dependencies/ ./
COPY --from=builder $BUILD_DIR/snapshot-dependencies/ ./
COPY --from=builder $BUILD_DIR/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]