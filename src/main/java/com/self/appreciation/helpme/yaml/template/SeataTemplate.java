// SeataTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SeataTemplate {
    // 基础配置
    private String image = "seataio/seata-server:latest";
    private String seataVersion = "1.5.2";
    private String storeMode = "file"; // file 或 db

    // 资源配置
    private String cpuRequest = "100m";
    private String cpuLimit = "250m";
    private String memoryRequest = "256Mi";
    private String memoryLimit = "512Mi";

    // 网络配置
    private Integer port = 8091;
    private String serviceType = "ClusterIP";

    // Seata配置
    private Integer replicas = 1;
    private String applicationId = "seata-server";
    private String txServiceGroup = "my-tx-group";
    private String configType = "file"; // file, nacos, apollo, consul, etcd3, zk
    private String registryType = "file"; // file, nacos, eureka, redis, zk, consul, etcd3, sofa

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 性能调优参数
    private String jvmXms = "256m";
    private String jvmXmx = "512m";
    private String jvmXmn = "128m";

    // 数据库配置（当 storeMode 为 db 时使用）
    private String dbHost = "mysql";
    private Integer dbPort = 3306;
    private String dbName = "seata";
    private String dbUser = "seata";
    private String dbPassword = "seata";

    // 注册中心配置
    private String registryNacosServerAddr = "nacos:8848";
    private String registryNacosGroup = "SEATA_GROUP";
    private String registryNacosNamespace = "";

    // 配置中心配置
    private String configNacosServerAddr = "nacos:8848";
    private String configNacosGroup = "SEATA_GROUP";
    private String configNacosNamespace = "";

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

    // 持久化配置
    private Boolean persistenceEnabled = false;
    private String storageSize = "1Gi";
    private String storageClassName;

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
    private Map<String, String> seataConfiguration;

    public static class Builder {
        private final SeataTemplate seataTemplate;

        public Builder(String project) {
            seataTemplate = new SeataTemplate();
        }

        public Builder image(String image) {
            seataTemplate.image = image;
            return this;
        }

        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            seataTemplate.cpuRequest = cpuRequest;
            seataTemplate.cpuLimit = cpuLimit;
            seataTemplate.memoryRequest = memoryRequest;
            seataTemplate.memoryLimit = memoryLimit;
            return this;
        }

        public Builder replicas(Integer replicas) {
            seataTemplate.replicas = replicas;
            return this;
        }

        public Builder port(Integer port) {
            seataTemplate.port = port;
            return this;
        }

        public Builder storeMode(String storeMode) {
            seataTemplate.storeMode = storeMode;
            return this;
        }

        public Builder database(String dbHost, Integer dbPort, String dbName, String dbUser, String dbPassword) {
            seataTemplate.dbHost = dbHost;
            seataTemplate.dbPort = dbPort;
            seataTemplate.dbName = dbName;
            seataTemplate.dbUser = dbUser;
            seataTemplate.dbPassword = dbPassword;
            return this;
        }

        public Builder registry(String registryType, String registryNacosServerAddr) {
            seataTemplate.registryType = registryType;
            seataTemplate.registryNacosServerAddr = registryNacosServerAddr;
            return this;
        }

        public Builder config(String configType, String configNacosServerAddr) {
            seataTemplate.configType = configType;
            seataTemplate.configNacosServerAddr = configNacosServerAddr;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            seataTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder seataConfiguration(Map<String, String> seataConfiguration) {
            seataTemplate.seataConfiguration = seataConfiguration;
            return this;
        }

        public SeataTemplate build() {
            return seataTemplate;
        }
    }
}
