package com.containersolutions.mesos.config.autoconfigure;

import com.containersolutions.mesos.HealthCheckFactory;
import com.containersolutions.mesos.controllers.CountController;
import com.containersolutions.mesos.scheduler.InstanceCount;
import com.containersolutions.mesos.scheduler.TaskInfoFactory;
import com.containersolutions.mesos.scheduler.TaskInfoFactoryCommand;
import com.containersolutions.mesos.scheduler.TaskInfoFactoryDocker;
import com.containersolutions.mesos.scheduler.config.FrameworkMesosConfigProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mesos.Protos;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({MesosSchedulerConfiguration.class})
public class FrameworkSchedulerConfiguration {
    protected final Log logger = LogFactory.getLog(getClass());

    @Bean
    @ConditionalOnMissingBean(HealthCheckFactory.class)
    public HealthCheckFactory healthCheckFactory() {
        return new HealthCheckFactory();
    }

    @Bean
    @ConditionalOnMissingBean(FrameworkMesosConfigProperties.class)
    public FrameworkMesosConfigProperties frameworkMesosConfigProperties() {
        return new FrameworkMesosConfigProperties();
    }

    @Bean(name = "defaultTaskInfoFactoryDocker")
    public TaskInfoFactoryDocker defaulTaskInfoFactoryDocker() {
        return new TaskInfoFactoryDocker();
    }

    @Bean(name = "defaultTaskInfoFactoryCommand")
    public TaskInfoFactoryCommand defaultTaskInfoFactoryCommand() {
        return new TaskInfoFactoryCommand();
    }

    // Overridden TaskInfoFactoryBean
    @Bean(name = "frameworkTaskInfoFactoryDocker")
    @ConditionalOnProperty(prefix = "mesos.docker", name = {"image"})
    @Primary
    public TaskInfoFactory taskInfoFactoryDocker(@Qualifier("defaultTaskInfoFactoryDocker") TaskInfoFactoryDocker taskInfoFactoryDocker, HealthCheckFactory healthCheckFactory) {
        return (taskId, offer, resources, executionParameters) -> {
            Protos.TaskInfo taskInfo = taskInfoFactoryDocker.create(taskId, offer, resources, executionParameters);
            Protos.TaskInfo built = Protos.TaskInfo.newBuilder()
                    .mergeFrom(taskInfo)
                    .setHealthCheck(healthCheckFactory.create(resources))
                    .build();
            logger.debug(built);
            return built;
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "frameworkTaskInfoFactoryDocker")
    @Primary
    public TaskInfoFactory taskInfoFactoryCommand(@Qualifier("defaultTaskInfoFactoryCommand") TaskInfoFactoryCommand taskInfoFactoryCommand, HealthCheckFactory healthCheckFactory) {
        return (taskId, offer, resources, executionParameters) -> {
            Protos.TaskInfo taskInfo = taskInfoFactoryCommand.create(taskId, offer, resources, executionParameters);
            Protos.TaskInfo built = Protos.TaskInfo.newBuilder()
                    .mergeFrom(taskInfo)
                    .setHealthCheck(healthCheckFactory.create(resources))
                    .build();
            logger.debug(built);
            return built;
        };
    }

    @Bean
    @ConditionalOnBean(value = InstanceCount.class)
    public CountController countController(InstanceCount instanceCount) {
        return new CountController(instanceCount);
    }
}
