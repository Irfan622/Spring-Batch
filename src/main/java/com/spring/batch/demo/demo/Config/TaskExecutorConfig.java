package com.spring.batch.demo.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TaskExecutorConfig {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecutorConfig.class);

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor() {
            @Override
            public void execute(Runnable task) {
                super.execute(new LoggingRunnable(task));
                logActiveThreadCount();
            }

            private void logActiveThreadCount() {
                int activeCount = this.getActiveCount();
                System.out.println("Active Threads: {}"+activeCount);

            }
        };
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("batch-thread-");
        taskExecutor.setRejectedExecutionHandler(new CustomRejectedExecutionHandler());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(30);
        taskExecutor.initialize();
        return taskExecutor;
    }

    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.error("Task {} rejected from {}", r.toString(), executor.toString());

        }
    }

    private static class LoggingRunnable implements Runnable {
        private final Runnable task;

        LoggingRunnable(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Task execution failed", e);
                // Implement additional handling logic here
            }
        }
    }
}
