package etl.engine.extract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@EnableAsync
public class AsyncConfiguration implements SchedulingConfigurer {

    @Bean(destroyMethod = "shutdown")
    public Executor etlThreadSchedulerPool() {
        return Executors.newScheduledThreadPool(3);
    }

    @Bean
    public Executor etlThreadAsyncPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadGroupName("etl-async");
        taskExecutor.setThreadNamePrefix("etl-async-");
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(3);
        taskExecutor.setQueueCapacity(3);
        return taskExecutor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(etlThreadSchedulerPool());
    }
}
