package com.containersolutions.mesos;

import com.containersolutions.mesos.scheduler.MesosProtoFactory;
import com.containersolutions.mesos.scheduler.config.FrameworkMesosConfigProperties;
import org.apache.mesos.Protos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HealthCheckFactory implements MesosProtoFactory<Protos.HealthCheck> {
    @Autowired
    FrameworkMesosConfigProperties mesosConfig;

    @Override
    public Protos.HealthCheck create(List<Protos.Resource> resources) {
        // Dev note: HealthCheck does not work in 0.25: https://issues.apache.org/jira/browse/MESOS-3738
        Protos.HealthCheck healthDefault = Protos.HealthCheck.getDefaultInstance();
        return Protos.HealthCheck.newBuilder()
                .setCommand(
                        Protos.CommandInfo.newBuilder()
                                .setValue(mesosConfig.getHealthCheck().getOrDefault("command", "echo Success"))
                )
                .setTimeoutSeconds(Double.valueOf(mesosConfig.getHealthCheck().getOrDefault("timeout", Double.toString(healthDefault.getTimeoutSeconds()))))
                .setDelaySeconds(Double.valueOf(mesosConfig.getHealthCheck().getOrDefault("delay", Double.toString(healthDefault.getDelaySeconds()))))
                .setConsecutiveFailures(Integer.valueOf(mesosConfig.getHealthCheck().getOrDefault("consecutiveFailures", Integer.toString(healthDefault.getConsecutiveFailures()))))
                .setGracePeriodSeconds(Double.valueOf(mesosConfig.getHealthCheck().getOrDefault("gracePeriod", Double.toString(healthDefault.getGracePeriodSeconds()))))
                .setIntervalSeconds(Double.valueOf(mesosConfig.getHealthCheck().getOrDefault("interval", Double.toString(healthDefault.getIntervalSeconds()))))
                .build();
    }
}
