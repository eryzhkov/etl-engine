package etl.engine.ems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
@EnableScheduling
@EnableAsync
public class AsyncConfiguration implements SchedulingConfigurer {

    @Bean(destroyMethod = "shutdown")
    public Executor etlThreadSchedulerPool() {
        ScheduledThreadPoolExecutor stpe = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
        stpe.setRemoveOnCancelPolicy(true);
        stpe.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        stpe.setThreadFactory(new CustomizableThreadFactory("scheduler-"));
        return stpe;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(etlThreadSchedulerPool());
    }
}
