// ConsulTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ConsulTemplate {
    // 基础配置
    private String image = "consul:1.16";
    private String domain = "consul";
    private String datacenter = "dc1";

    // 资源配置
    private String cpuRequest = "100m";
    private String cpuLimit = "200m";
    private String memoryRequest = "128Mi";
    private String memoryLimit = "256Mi";

    // 存储配置
    private String storageSize = "1Gi";
    private String storageClassName;

    // 网络配置
    private Integer serverPort = 8300;
    private Integer serfLanPort = 8301;
    private Integer serfWanPort = 8302;
    private Integer clientPort = 8500;
    private Integer dnsPort = 8600;
    private String serviceType = "ClusterIP";

    // Consul配置
    private Integer replicas = 1;
    private Boolean server = true;
    private String bootstrapExpect = "1";
    private String ui = "true";
    private String retryJoin = "";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String gossipProtocolVersion = "1";
    private String raftProtocolVersion = "3";
    private String rpcProtocolVersion = "2";

    // 安全配置
    private Boolean tlsEnabled = false;
    private String tlsSecretName;
    private Boolean aclEnabled = false;
    private String aclDefaultPolicy = "allow";
    private String aclDownPolicy = "extend-cache";

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
    private Map<String, String> consulConfiguration;

    public static class Builder {
        private final ConsulTemplate consulTemplate;

        public Builder(String project) {
            consulTemplate = new ConsulTemplate();
        }

        public Builder image(String image) {
            consulTemplate.image = image;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            consulTemplate.cpuRequest = cpuRequest;
            consulTemplate.cpuLimit = cpuLimit;
            consulTemplate.memoryRequest = memoryRequest;
            consulTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder storage(String size) {
            consulTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            consulTemplate.storageSize = size;
            consulTemplate.storageClassName = storageClassName;
            return this;
        }

        public Builder replicas(Integer replicas) {
            consulTemplate.replicas = replicas;
            return this;
        }

        public Builder ports(Integer serverPort, Integer clientPort, Integer dnsPort) {
            consulTemplate.serverPort = serverPort;
            consulTemplate.clientPort = clientPort;
            consulTemplate.dnsPort = dnsPort;
            return this;
        }

        public Builder configuration(String datacenter, String domain) {
            consulTemplate.datacenter = datacenter;
            consulTemplate.domain = domain;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            consulTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder consulConfiguration(Map<String, String> consulConfiguration) {
            consulTemplate.consulConfiguration = consulConfiguration;
            return this;
        }

        public ConsulTemplate build() {
            return consulTemplate;
        }
    }
}
