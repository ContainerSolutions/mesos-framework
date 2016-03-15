package com.containersolutions.mesos.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "mesos")
public class FrameworkMesosConfigProperties extends MesosConfigProperties {
    private Map<String, String> healthCheck;

    public Map<String, String> getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(Map<String, String> healthCheck) {
        this.healthCheck = healthCheck;
    }
}
