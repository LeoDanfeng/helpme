// RocketMQTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class RocketMQTemplate {
    // 基础配置
    private String image = "apache/rocketmq:latest";
    private String namesrvImage = "apache/rocketmq:latest";
    private String brokerImage = "apache/rocketmq:latest";

    // 资源配置
    private String namesrvCpuRequest = "250m";
    private String namesrvCpuLimit = "500m";
    private String namesrvMemoryRequest = "256Mi";
    private String namesrvMemoryLimit = "512Mi";
    private String brokerCpuRequest = "500m";
    private String brokerCpuLimit = "1000m";
    private String brokerMemoryRequest = "1Gi";
    private String brokerMemoryLimit = "2Gi";

    // 存储配置
    private String storageSize = "10Gi";
    private String storageClassName;

    // 网络配置
    private Integer namesrvPort = 9876;
    private Integer brokerPort = 10911;
    private Integer brokerHaPort = 10912;
    private String serviceType = "ClusterIP";

    // RocketMQ配置
    private Integer namesrvReplicas = 1;
    private Integer brokerReplicas = 1;
    private String clusterName = "DefaultCluster";
    private String brokerName = "broker-a";
    private Integer brokerId = 0;

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String maxMessageSize = "131072";
    private String flushDiskType = "ASYNC_FLUSH";
    private String storePathRootDir = "/home/rocketmq/store";
    private String deleteWhen = "04";
    private String fileReservedTime = "48";
    private String brokerRole = "ASYNC_MASTER";

    // 安全配置
    private Boolean authenticationEnabled = false;
    private Boolean authorizationEnabled = false;
    private String accessKey;
    private String secretKey;

    // 健康检查配置
    private Integer livenessProbeInitialDelaySeconds = 60;
    private Integer livenessProbePeriodSeconds = 10;
    private Integer livenessProbeTimeoutSeconds = 5;
    private Integer readinessProbeInitialDelaySeconds = 30;
    private Integer readinessProbePeriodSeconds = 10;
    private Integer readinessProbeTimeoutSeconds = 3;

    // 持久化配置
    private Boolean persistenceEnabled = true;
    private String persistenceSubPath;

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

    // 自定义配置
    private Map<String, String> brokerConfiguration;
    private Map<String, String> namesrvConfiguration;

    public static class Builder {
        private final RocketMQTemplate rocketMQTemplate;

        public Builder(String project) {
            rocketMQTemplate = new RocketMQTemplate();
        }

        public Builder images(String namesrvImage, String brokerImage) {
            rocketMQTemplate.namesrvImage = namesrvImage;
            rocketMQTemplate.brokerImage = brokerImage;
            return this;
        }

        public Builder namesrvResources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            rocketMQTemplate.namesrvCpuRequest = cpuRequest;
            rocketMQTemplate.namesrvCpuLimit = cpuLimit;
            rocketMQTemplate.namesrvMemoryRequest = memoryRequest;
            rocketMQTemplate.namesrvMemoryLimit = memoryLimit;
            return this;
        }

        public Builder brokerResources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            rocketMQTemplate.brokerCpuRequest = cpuRequest;
            rocketMQTemplate.brokerCpuLimit = cpuLimit;
            rocketMQTemplate.brokerMemoryRequest = memoryRequest;
            rocketMQTemplate.brokerMemoryLimit = memoryLimit;
            return this;
        }

        public Builder storage(String size) {
            rocketMQTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            rocketMQTemplate.storageSize = size;
            rocketMQTemplate.storageClassName = storageClassName;
            return this;
        }

        public Builder replicas(Integer namesrvReplicas, Integer brokerReplicas) {
            rocketMQTemplate.namesrvReplicas = namesrvReplicas;
            rocketMQTemplate.brokerReplicas = brokerReplicas;
            return this;
        }

        public Builder ports(Integer namesrvPort, Integer brokerPort) {
            rocketMQTemplate.namesrvPort = namesrvPort;
            rocketMQTemplate.brokerPort = brokerPort;
            return this;
        }

        public Builder cluster(String clusterName, String brokerName) {
            rocketMQTemplate.clusterName = clusterName;
            rocketMQTemplate.brokerName = brokerName;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            rocketMQTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder brokerConfiguration(Map<String, String> brokerConfiguration) {
            rocketMQTemplate.brokerConfiguration = brokerConfiguration;
            return this;
        }

        public RocketMQTemplate build() {
            return rocketMQTemplate;
        }
    }
}
