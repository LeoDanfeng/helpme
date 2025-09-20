// NacosTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class NacosTemplate {
    // 基础配置
    private String image = "nacos/nacos-server:latest";
    private String nacosAuthEnable = "false";
    private String nacosAuthSystemType = "nacos";
    private String nacosAuthUsername = "nacos";
    private String nacosAuthPassword = "nacos";

    // 资源配置
    private String cpuRequest = "250m";
    private String cpuLimit = "500m";
    private String memoryRequest = "512Mi";
    private String memoryLimit = "1Gi";

    // 存储配置
    private String storageSize = "5Gi";
    private String storageClassName;

    // 网络配置
    private Integer clientPort = 8848;
    private Integer serverPort = 9848;
    private Integer raftPort = 9849;
    private String serviceType = "ClusterIP";

    // Nacos配置
    private Integer replicas = 1;
    private String mode = "standalone"; // standalone 或 cluster
    private String serverAddr = "";
    private String preferHostMode = "hostname";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String jvmXms = "512m";
    private String jvmXmx = "512m";
    private String jvmXmn = "256m";
    private String jvmMs = "32m";
    private String jvmMms = "64m";
    private String jvmOther = "-XX:ParallelGCThreads=4 -XX:+UseG1GC";

    // 数据库配置
    private String dbHost = "mysql";
    private Integer dbPort = 3306;
    private String dbName = "nacos";
    private String dbUser = "nacos";
    private String dbPassword = "nacos";
    private Boolean embeddedStorage = true; // 使用嵌入式数据库

    // 安全配置
    private Boolean tlsEnabled = false;
    private String tlsSecretName;
    private Boolean authEnabled = false;

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
    private Map<String, String> nacosConfiguration;

    public static class Builder {
        private final NacosTemplate nacosTemplate;

        public Builder(String project) {
            nacosTemplate = new NacosTemplate();
        }

        public Builder image(String image) {
            nacosTemplate.image = image;
            return this;
        }

        public Builder credentials(String username, String password) {
            nacosTemplate.nacosAuthUsername = username;
            nacosTemplate.nacosAuthPassword = password;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            nacosTemplate.cpuRequest = cpuRequest;
            nacosTemplate.cpuLimit = cpuLimit;
            nacosTemplate.memoryRequest = memoryRequest;
            nacosTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder storage(String size) {
            nacosTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            nacosTemplate.storageSize = size;
            nacosTemplate.storageClassName = storageClassName;
            return this;
        }

        public Builder replicas(Integer replicas) {
            nacosTemplate.replicas = replicas;
            return this;
        }

        public Builder mode(String mode) {
            nacosTemplate.mode = mode;
            return this;
        }

        public Builder ports(Integer clientPort, Integer serverPort, Integer raftPort) {
            nacosTemplate.clientPort = clientPort;
            nacosTemplate.serverPort = serverPort;
            nacosTemplate.raftPort = raftPort;
            return this;
        }

        public Builder database(Boolean embeddedStorage, String dbHost, Integer dbPort,
                               String dbName, String dbUser, String dbPassword) {
            nacosTemplate.embeddedStorage = embeddedStorage;
            nacosTemplate.dbHost = dbHost;
            nacosTemplate.dbPort = dbPort;
            nacosTemplate.dbName = dbName;
            nacosTemplate.dbUser = dbUser;
            nacosTemplate.dbPassword = dbPassword;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            nacosTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder nacosConfiguration(Map<String, String> nacosConfiguration) {
            nacosTemplate.nacosConfiguration = nacosConfiguration;
            return this;
        }

        public NacosTemplate build() {
            return nacosTemplate;
        }
    }
}
