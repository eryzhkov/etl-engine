package etl.engine.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EtlManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtlManagementServiceApplication.class, args);
    }
}