// ZookeeperTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ZookeeperTemplate {
    // 基础配置
    private String image = "zookeeper:3.8";
    private String servers = "server.1=zookeeper-0.zookeeper-headless:2888:3888";

    // 资源配置
    private String cpuRequest = "100m";
    private String cpuLimit = "250m";
    private String memoryRequest = "128Mi";
    private String memoryLimit = "256Mi";

    // 存储配置
    private String storageSize = "5Gi";
    private String storageClassName;

    // 网络配置
    private Integer clientPort = 2181;
    private Integer serverPort = 2888;
    private Integer electionPort = 3888;
    private String serviceType = "ClusterIP";

    // Zookeeper配置
    private Integer replicas = 1;
    private String tickTime = "2000";
    private String initLimit = "10";
    private String syncLimit = "5";
    private String maxClientCnxns = "60";
    private String snapRetainCount = "3";
    private String purgeInterval = "1";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String jvmFlags = "-Xmx256m -Xms128m";
    private String maxSessionTimeout = "40000";
    private String minSessionTimeout = "4000";
    private String autopurgeSnapRetainCount = "3";
    private String autopurgePurgeInterval = "1";

    // 安全配置
    private Boolean authenticationEnabled = false;
    private String digest = "admin:admin123";
    private Boolean secureClientPortEnabled = false;
    private Integer secureClientPort = 2281;

    // 健康检查配置
    private Integer livenessProbeInitialDelaySeconds = 30;
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
    private Map<String, String> zookeeperConfiguration;

    public static class Builder {
        private final ZookeeperTemplate zookeeperTemplate;

        public Builder(String project) {
            zookeeperTemplate = new ZookeeperTemplate();
        }

        public Builder image(String image) {
            zookeeperTemplate.image = image;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            zookeeperTemplate.cpuRequest = cpuRequest;
            zookeeperTemplate.cpuLimit = cpuLimit;
            zookeeperTemplate.memoryRequest = memoryRequest;
            zookeeperTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder storage(String size) {
            zookeeperTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            zookeeperTemplate.storageSize = size;
            zookeeperTemplate.storageClassName = storageClassName;
            return this;
        }

        public Builder replicas(Integer replicas) {
            zookeeperTemplate.replicas = replicas;
            return this;
        }

        public Builder ports(Integer clientPort, Integer serverPort, Integer electionPort) {
            zookeeperTemplate.clientPort = clientPort;
            zookeeperTemplate.serverPort = serverPort;
            zookeeperTemplate.electionPort = electionPort;
            return this;
        }

        public Builder configuration(String tickTime, String initLimit, String syncLimit) {
            zookeeperTemplate.tickTime = tickTime;
            zookeeperTemplate.initLimit = initLimit;
            zookeeperTemplate.syncLimit = syncLimit;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            zookeeperTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder zookeeperConfiguration(Map<String, String> zookeeperConfiguration) {
            zookeeperTemplate.zookeeperConfiguration = zookeeperConfiguration;
            return this;
        }

        public ZookeeperTemplate build() {
            return zookeeperTemplate;
        }
    }
}
