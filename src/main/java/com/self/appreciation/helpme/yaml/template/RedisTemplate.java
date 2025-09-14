// RedisTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class RedisTemplate {
    // 基础配置
    private String image = "redis:6.2";
    private String redisPassword = "requirepasswd";
    private String bind = "0.0.0.0";
    private String redisPort = "6379";

    // 资源配置
    private String cpuRequest = "100n";
    private String cpuLimit = "200m";
    private String memoryRequest = "128Mi";
    private String memoryLimit = "256Mi";

    // 存储配置
    private String storageSize = "1Gi";
    private String storageClassName;

    // 网络配置
    private Integer containerPort = 6379;
    private String serviceType = "ClusterIP";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // Redis 配置选项
    private String maxMemory = "256mb";
    private String maxMemoryPolicy = "allkeys-lru";
    private Integer timeout = 0;
    private Integer tcpKeepalive = 300;
    private String databases = "16";

    // 性能调优参数
    private String tcpBacklog = "511";
    private String maxClients = "10000";
    private String maxMemorySamples = "5";
    private String _hz = "10";
    private String save = "900 1 300 10 60 10000"; // 快照策略

    // 安全配置
    private Boolean enableAcl = false;
    private String aclFile = "/etc/redis/users.acl";
    private Boolean renameCommands = false;

    // 持久化配置
    private Boolean persistenceEnabled = true;
    private String persistenceSubPath;
    private Boolean appendOnly = true; // AOF 持久化
    private String appendFsync = "everysec";

    // 初始化配置
    private Map<String, String> redisConfiguration; // redis.conf 配置项
    private List<String> initScripts; // 初始化脚本内容

    // 健康检查配置
    private Integer livenessProbeInitialDelaySeconds = 30;
    private Integer livenessProbePeriodSeconds = 10;
    private Integer livenessProbeTimeoutSeconds = 5;
    private Integer readinessProbeInitialDelaySeconds = 5;
    private Integer readinessProbePeriodSeconds = 10;
    private Integer readinessProbeTimeoutSeconds = 1;

    // 副本数配置
    private Integer replicas = 1;

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

    // 启动参数
    private List<String> extraArgs;

    // 备份配置
    private Boolean backupEnabled = false;
    private String backupSchedule = "0 2 * * *"; // 默认每天凌晨2点备份
    private String backupRetention = "7"; // 保留7天备份

    // 集群配置
    private Boolean clusterMode = false;
    private Integer clusterNodes = 3;

    public static class Builder {
        private final RedisTemplate redisTemplate;

        public Builder(String project) {
            redisTemplate = new RedisTemplate();
        }

        // 基础配置构建器方法
        public Builder image(String image) {
            redisTemplate.image = image;
            return this;
        }

        public Builder redisPassword(String redisPassword) {
            redisTemplate.redisPassword = redisPassword;
            return this;
        }

        public Builder redisPort(String redisPort) {
            redisTemplate.redisPort = redisPort;
            return this;
        }

        // 资源配置构建器方法
        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            redisTemplate.cpuRequest = cpuRequest;
            redisTemplate.cpuLimit = cpuLimit;
            redisTemplate.memoryRequest = memoryRequest;
            redisTemplate.memoryLimit = memoryLimit;
            return this;
        }

        // 存储配置构建器方法
        public Builder storage(String size) {
            redisTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            redisTemplate.storageSize = size;
            redisTemplate.storageClassName = storageClassName;
            return this;
        }

        // 网络配置构建器方法
        public Builder port(Integer port) {
            redisTemplate.containerPort = port;
            return this;
        }

        public Builder serviceType(String serviceType) {
            redisTemplate.serviceType = serviceType;
            return this;
        }

        // 性能配置构建器方法
        public Builder performance(String maxMemory, String maxClients) {
            redisTemplate.maxMemory = maxMemory;
            redisTemplate.maxClients = maxClients;
            return this;
        }

        // 健康检查配置构建器方法
        public Builder probes(Integer livenessInitial, Integer livenessPeriod,
                              Integer readinessInitial, Integer readinessPeriod) {
            redisTemplate.livenessProbeInitialDelaySeconds = livenessInitial;
            redisTemplate.livenessProbePeriodSeconds = livenessPeriod;
            redisTemplate.readinessProbeInitialDelaySeconds = readinessInitial;
            redisTemplate.readinessProbePeriodSeconds = readinessPeriod;
            return this;
        }

        // 副本配置构建器方法
        public Builder replicas(Integer replicas) {
            redisTemplate.replicas = replicas;
            return this;
        }

        // 高级配置构建器方法
        public Builder persistence(Boolean enable) {
            redisTemplate.persistenceEnabled = enable;
            return this;
        }

        public Builder backup(Boolean enable, String schedule) {
            redisTemplate.backupEnabled = enable;
            redisTemplate.backupSchedule = schedule;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            redisTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder redisConfiguration(Map<String, String> redisConfiguration) {
            redisTemplate.redisConfiguration = redisConfiguration;
            return this;
        }

        public Builder cluster(Boolean enable, Integer nodes) {
            redisTemplate.clusterMode = enable;
            redisTemplate.clusterNodes = nodes;
            return this;
        }

        public RedisTemplate build() {
            return redisTemplate;
        }
    }
}
