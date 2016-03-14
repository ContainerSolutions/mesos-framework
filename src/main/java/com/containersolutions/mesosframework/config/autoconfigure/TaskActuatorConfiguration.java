package com.containersolutions.mesosframework.config.autoconfigure;

import com.containersolutions.mesos.config.autoconfigure.MesosSchedulerConfiguration;
import com.containersolutions.mesos.scheduler.events.StatusUpdateEvent;
import com.containersolutions.mesos.scheduler.state.StateRepository;
import org.apache.mesos.Protos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(HealthIndicator.class)
@AutoConfigureBefore({org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration.class})
@AutoConfigureAfter({MesosSchedulerConfiguration.class})
public class TaskActuatorConfiguration implements ApplicationListener<StatusUpdateEvent> {
    @Autowired
    StateRepository stateRepository;

    @Value("${mesos.resources.scale:1}")
    Integer scale;

    protected Map<String, Protos.TaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Bean
    public HealthIndicator taskHealthIndicator() {
        return new AbstractHealthIndicator() {
            @Override
            protected void doHealthCheck(Health.Builder builder) throws Exception {
                if (correctNumberOfInstances()) {
                    builder.up();
                } else {
                    builder.down();
                }
                builder.withDetail("expectedInstances", scale);
                builder.withDetail("instances", stateRepository.allTaskInfos().size());

                for (Protos.TaskState taskState : Protos.TaskState.values()) {
                    Map<String, Protos.TaskStatus> state = getTasksForState(taskState);
                    builder.withDetail(taskState.name(), state.size());
                }

            }
        };
    }

    @Override
    public void onApplicationEvent(StatusUpdateEvent event) {
        updateTaskStatusList(event);
        List<String> taskIdStrings = getStoredTaskList();
        if (correctNumberOfInstances()) {    // If cluster is valid, remove old tasks.
            taskStatusMap = taskStatusMap.entrySet().stream()
                    .filter(entry -> taskIdStrings.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private boolean correctNumberOfInstances() {
        return stateRepository.allTaskInfos().size() == scale;
    }

    private void updateTaskStatusList(StatusUpdateEvent event) {
        taskStatusMap.put(getKey(event.getTaskStatus().getTaskId()), event.getTaskStatus());
    }

    private List<String> getStoredTaskList() {
        return stateRepository.allTaskInfos().stream().map(taskInfo -> getKey(taskInfo.getTaskId())).collect(Collectors.toList());
    }

    private String getKey(Protos.TaskID taskId) {
        return taskId.getValue();
    }

    private Map<String, Protos.TaskStatus> getTasksForState(Protos.TaskState state) {
        return taskStatusMap.entrySet().stream()
                .filter(entry -> entry.getValue().getState().equals(state))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

