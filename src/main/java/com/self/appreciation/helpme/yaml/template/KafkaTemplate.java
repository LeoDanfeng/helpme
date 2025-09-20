// KafkaTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class KafkaTemplate {
    // 基础配置
    private String image = "bitnami/kafka:latest";
    private String kafkaUsername;
    private String kafkaPassword;

    // 资源配置
    private String cpuRequest = "500m";
    private String cpuLimit = "1000m";
    private String memoryRequest = "1Gi";
    private String memoryLimit = "2Gi";

    // 存储配置
    private String storageSize = "10Gi";
    private String storageClassName;

    // 网络配置
    private Integer containerPort = 9092;
    private String serviceType = "ClusterIP";
    private Integer externalPort = 9094;

    // Kafka配置
    private Integer replicas = 3;
    private String zookeeperConnect = "zookeeper:2181";
    private String logRetentionHours = "168";
    private String logRetentionBytes = "1073741824";
    private String logSegmentBytes = "1073741824";
    private String numPartitions = "1";
    private String defaultReplicationFactor = "1";
    private String offsetsTopicReplicationFactor = "1";
    private String transactionStateLogReplicationFactor = "1";
    private String transactionStateLogMinIsr = "1";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 监听器配置
    private Boolean allowPlaintextListener = true;
    private String listeners = "PLAINTEXT://:9092";
    private String advertisedListeners;
    private String listenerSecurityProtocolMap = "PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT";
    private String interBrokerListenerName = "PLAINTEXT";

    // 性能调优参数
    private String heapOpts = "-Xmx1g -Xms1g";
    private String socketSendBufferBytes = "102400";
    private String socketReceiveBufferBytes = "102400";
    private String socketRequestMaxBytes = "104857600";
    private String maxMessageBytes = "1048576";

    // 安全配置
    private Boolean authenticationEnabled = false;
    private Boolean authorizationEnabled = false;
    private String saslMechanism = "PLAIN";
    private String securityProtocol = "SASL_PLAINTEXT";

    // 健康检查配置
    private Integer livenessProbeInitialDelaySeconds = 30;
    private Integer livenessProbePeriodSeconds = 10;
    private Integer livenessProbeTimeoutSeconds = 5;
    private Integer readinessProbeInitialDelaySeconds = 5;
    private Integer readinessProbePeriodSeconds = 10;
    private Integer readinessProbeTimeoutSeconds = 1;

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
    private Map<String, String> serverConfiguration;

    public static class Builder {
        private final KafkaTemplate kafkaTemplate;

        public Builder(String project) {
            kafkaTemplate = new KafkaTemplate();
        }

        public Builder image(String image) {
            kafkaTemplate.image = image;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            kafkaTemplate.cpuRequest = cpuRequest;
            kafkaTemplate.cpuLimit = cpuLimit;
            kafkaTemplate.memoryRequest = memoryRequest;
            kafkaTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder storage(String size) {
            kafkaTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            kafkaTemplate.storageSize = size;
            kafkaTemplate.storageClassName = storageClassName;
            return this;
        }

        public Builder replicas(Integer replicas) {
            kafkaTemplate.replicas = replicas;
            return this;
        }

        public Builder zookeeperConnect(String zookeeperConnect) {
            kafkaTemplate.zookeeperConnect = zookeeperConnect;
            return this;
        }

        public Builder ports(Integer internalPort, Integer externalPort) {
            kafkaTemplate.containerPort = internalPort;
            kafkaTemplate.externalPort = externalPort;
            return this;
        }

        public Builder authentication(Boolean enabled) {
            kafkaTemplate.authenticationEnabled = enabled;
            return this;
        }

        public Builder authorization(Boolean enabled) {
            kafkaTemplate.authorizationEnabled = enabled;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            kafkaTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder serverConfiguration(Map<String, String> serverConfiguration) {
            kafkaTemplate.serverConfiguration = serverConfiguration;
            return this;
        }

        public KafkaTemplate build() {
            return kafkaTemplate;
        }
    }
}
