// RabbitMQTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class RabbitMQTemplate {
    // 基础配置
    private String image = "rabbitmq:3-management";
    private String rabbitmqUser = "user";
    private String rabbitmqPassword = "password";
    private String rabbitmqErlangCookie = "secret-cookie";

    // 资源配置
    private String cpuRequest = "250m";
    private String cpuLimit = "500m";
    private String memoryRequest = "256Mi";
    private String memoryLimit = "512Mi";

    // 存储配置
    private String storageSize = "5Gi";
    private String storageClassName;

    // 网络配置
    private Integer amqpPort = 5672;
    private Integer managementPort = 15672;
    private String serviceType = "ClusterIP";

    // RabbitMQ配置
    private Integer replicas = 1;
    private String defaultUser = "user";
    private String defaultPass = "password";
    private Boolean clusteringEnabled = false;
    private String clusterName = "rabbitmq-cluster";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String vmMemoryHighWatermark = "0.4";
    private String diskFreeLimit = "2GB";
    private String frameMax = "131072";
    private String heartbeat = "60";
    private String channelMax = "128";

    // 插件配置
    private List<String> plugins = List.of("rabbitmq_management", "rabbitmq_peer_discovery_k8s");

    // 安全配置
    private Boolean tlsEnabled = false;
    private String tlsSecretName;
    private Boolean authEnabled = true;

    // 健康检查配置
    private Integer livenessProbeInitialDelaySeconds = 120;
    private Integer livenessProbePeriodSeconds = 10;
    private Integer livenessProbeTimeoutSeconds = 5;
    private Integer readinessProbeInitialDelaySeconds = 10;
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
    private Map<String, String> rabbitmqConfiguration;
    private String rabbitmqConf;

    public static class Builder {
        private final RabbitMQTemplate rabbitMQTemplate;

        public Builder(String project) {
            rabbitMQTemplate = new RabbitMQTemplate();
        }

        public Builder image(String image) {
            rabbitMQTemplate.image = image;
            return this;
        }

        public Builder credentials(String user, String password) {
            rabbitMQTemplate.rabbitmqUser = user;
            rabbitMQTemplate.rabbitmqPassword = password;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            rabbitMQTemplate.cpuRequest = cpuRequest;
            rabbitMQTemplate.cpuLimit = cpuLimit;
            rabbitMQTemplate.memoryRequest = memoryRequest;
            rabbitMQTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder storage(String size) {
            rabbitMQTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            rabbitMQTemplate.storageSize = size;
            rabbitMQTemplate.storageClassName = storageClassName;
            return this;
        }

        public Builder replicas(Integer replicas) {
            rabbitMQTemplate.replicas = replicas;
            return this;
        }

        public Builder clustering(Boolean enabled, String clusterName) {
            rabbitMQTemplate.clusteringEnabled = enabled;
            rabbitMQTemplate.clusterName = clusterName;
            return this;
        }

        public Builder ports(Integer amqpPort, Integer managementPort) {
            rabbitMQTemplate.amqpPort = amqpPort;
            rabbitMQTemplate.managementPort = managementPort;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            rabbitMQTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder rabbitmqConfiguration(Map<String, String> rabbitmqConfiguration) {
            rabbitMQTemplate.rabbitmqConfiguration = rabbitmqConfiguration;
            return this;
        }

        public RabbitMQTemplate build() {
            return rabbitMQTemplate;
        }
    }
}
