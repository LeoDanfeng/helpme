// SentinelTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SentinelTemplate {
    // 基础配置
    private String image = "sentinel-dashboard:latest";
    private String sentinelDashboardVersion = "1.8.4";
    private String sentinelAuthUsername = "sentinel";
    private String sentinelAuthPassword = "sentinel";

    // 资源配置
    private String cpuRequest = "100m";
    private String cpuLimit = "250m";
    private String memoryRequest = "256Mi";
    private String memoryLimit = "512Mi";

    // 网络配置
    private Integer dashboardPort = 8080;
    private String serviceType = "ClusterIP";

    // Sentinel配置
    private Integer replicas = 1;
    private String projectName = "sentinel-dashboard";
    private String authEnabled = "false";
    private String sessionTimeout = "7200";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String jvmXms = "256m";
    private String jvmXmx = "512m";
    private String jvmXmn = "128m";

    // 数据存储配置
    private Boolean persistenceEnabled = false;
    private String storageSize = "1Gi";
    private String storageClassName;

    // 安全配置
    private Boolean tlsEnabled = false;
    private String tlsSecretName;

    // 健康检查配置
    private Integer livenessProbeInitialDelaySeconds = 60;
    private Integer livenessProbePeriodSeconds = 10;
    private Integer livenessProbeTimeoutSeconds = 5;
    private Integer readinessProbeInitialDelaySeconds = 30;
    private Integer readinessProbePeriodSeconds = 10;
    private Integer readinessProbeTimeoutSeconds = 3;

    // 节点选择器
    private Map<String, String> nodeSelector;

    // 容忍度配置
    private List<Map<String, Object>> tolerations;

    // 亲和性配置
    private Map<String, Object> affinity;

    // 安全上下文
    private Map<String, Object> securityContext;

    // 容器安全上下文
    private Map<String, Object> containerSecurityContext;

    // 服务注解
    private Map<String, String> serviceAnnotations;

    // Pod注解
    private Map<String, String> podAnnotations;

    // Pod标签
    private Map<String, String> podLabels;

    // 优先级类
    private String priorityClassName;

    public static class Builder {
        private final SentinelTemplate sentinelTemplate;

        public Builder(String project) {
            sentinelTemplate = new SentinelTemplate();
        }

        public Builder image(String image) {
            sentinelTemplate.image = image;
            return this;
        }

        public Builder credentials(String username, String password) {
            sentinelTemplate.sentinelAuthUsername = username;
            sentinelTemplate.sentinelAuthPassword = password;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            sentinelTemplate.cpuRequest = cpuRequest;
            sentinelTemplate.cpuLimit = cpuLimit;
            sentinelTemplate.memoryRequest = memoryRequest;
            sentinelTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder replicas(Integer replicas) {
            sentinelTemplate.replicas = replicas;
            return this;
        }

        public Builder port(Integer dashboardPort) {
            sentinelTemplate.dashboardPort = dashboardPort;
            return this;
        }

        public Builder auth(Boolean enabled, String username, String password) {
            sentinelTemplate.authEnabled = enabled.toString();
            sentinelTemplate.sentinelAuthUsername = username;
            sentinelTemplate.sentinelAuthPassword = password;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            sentinelTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public SentinelTemplate build() {
            return sentinelTemplate;
        }
    }
}
