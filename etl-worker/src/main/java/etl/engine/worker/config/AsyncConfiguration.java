package etl.engine.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
        //return Executors.newScheduledThreadPool(3);
        ScheduledThreadPoolExecutor stpe = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
        stpe.setRemoveOnCancelPolicy(true);
        stpe.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        stpe.setThreadFactory(new CustomizableThreadFactory("scheduler-"));
        return stpe;
    }

    @Bean
    public Executor etlThreadAsyncPool() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadGroupName("etl-async");
        taskExecutor.setThreadNamePrefix("async-");
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
