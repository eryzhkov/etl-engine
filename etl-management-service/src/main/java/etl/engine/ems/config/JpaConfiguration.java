package etl.engine.ems.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "etl.engine.ems.dao")
@EnableJpaAuditing(dateTimeProviderRef = "auditingCustomOffsetDateTimeProvider")
@Slf4j
public class JpaConfiguration {

    // The Spring JPA Audit doesn't understand the OffsetDataTime class.
    // The only way to fix it is to provide the custom provider.
    @Bean(name = "auditingCustomOffsetDateTimeProvider")
    public DateTimeProvider customOffsetDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
