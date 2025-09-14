// MysqlTemplate.java
package com.self.appreciation.helpme.yaml.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class MysqlTemplate {
    // 基础配置
    private String image = "mysql:8.0";
    private String mysqlRootPassword = "MysqlRootPassword";
    private String mysqlUser = "mysqlUser";
    private String mysqlPassword = "MysqlPassword";
    private String mysqlDatabase = "application";

    // 资源配置
    private String cpuRequest = "500m";
    private String cpuLimit = "1000m";
    private String memoryRequest = "512Mi";
    private String memoryLimit = "1Gi";

    // 存储配置
    private String storageSize = "5Gi";
    private String storageClassName;

    // 网络配置
    private Integer containerPort = 3306;
    private String serviceType = "ClusterIP";

    // 环境变量配置
    private Map<String, String> extraEnvs;

    // 配置选项
    private String mysqlRootHost = "%";  // 允许root用户从任何主机连接
    private String characterSetServer = "utf8mb4";
    private String collationServer = "utf8mb4_unicode_ci";
    private String defaultAuthenticationPlugin = "mysql_native_password";

    // 性能调优参数
    private String innodbBufferPoolSize = "128M";
    private String maxConnections = "200";
    private String maxAllowedPacket = "16M";
    private String waitTimeout = "28800";
    private String interactiveTimeout = "28800";

    // 安全配置
    private Boolean skipNameResolve = true;  // 跳过域名解析
    private String sqlMode;  // SQL模式

    // 备份和恢复相关
    private Boolean enableBinlog = false;
    private String binlogFormat = "ROW";
    private String expireLogsDays = "7";

    // 初始化脚本
    private List<String> initSqlScripts;  // 初始化SQL脚本内容
    private Map<String, String> initSqlScriptsConfigMap;  // 指向ConfigMap的初始化脚本

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

    // 自定义配置
    private Map<String, String> mysqlConfiguration;  // my.cnf 配置项

    // 备份配置
    private Boolean backupEnabled = false;
    private String backupSchedule = "0 3 * * *";  // 默认每天凌晨3点备份
    private String backupRetention = "7";  // 保留7天备份

    public static class Builder {
        private final MysqlTemplate mysqlTemplate;

        public Builder(String project) {
            mysqlTemplate = new MysqlTemplate();
        }

        // 基础配置构建器方法
        public Builder image(String image) {
            mysqlTemplate.image = image;
            return this;
        }

        public Builder mysqlRootPassword(String mysqlRootPassword) {
            mysqlTemplate.mysqlRootPassword = mysqlRootPassword;
            return this;
        }

        public Builder mysqlUser(String mysqlUser) {
            mysqlTemplate.mysqlUser = mysqlUser;
            return this;
        }

        public Builder mysqlPassword(String mysqlPassword) {
            mysqlTemplate.mysqlPassword = mysqlPassword;
            return this;
        }

        public Builder mysqlDatabase(String mysqlDatabase) {
            mysqlTemplate.mysqlDatabase = mysqlDatabase;
            return this;
        }

        // 资源配置构建器方法
        public Builder resources(String cpuRequest, String cpuLimit, String memoryRequest, String memoryLimit) {
            mysqlTemplate.cpuRequest = cpuRequest;
            mysqlTemplate.cpuLimit = cpuLimit;
            mysqlTemplate.memoryRequest = memoryRequest;
            mysqlTemplate.memoryLimit = memoryLimit;
            return this;
        }

        // 存储配置构建器方法
        public Builder storage(String size) {
            mysqlTemplate.storageSize = size;
            return this;
        }

        public Builder storage(String size, String storageClassName) {
            mysqlTemplate.storageSize = size;
            mysqlTemplate.storageClassName = storageClassName;
            return this;
        }

        // 网络配置构建器方法
        public Builder port(Integer port) {
            mysqlTemplate.containerPort = port;
            return this;
        }

        public Builder serviceType(String serviceType) {
            mysqlTemplate.serviceType = serviceType;
            return this;
        }

        // 性能配置构建器方法
        public Builder performance(String innodbBufferPoolSize, String maxConnections) {
            mysqlTemplate.innodbBufferPoolSize = innodbBufferPoolSize;
            mysqlTemplate.maxConnections = maxConnections;
            return this;
        }

        // 健康检查配置构建器方法
        public Builder probes(Integer livenessInitial, Integer livenessPeriod,
                             Integer readinessInitial, Integer readinessPeriod) {
            mysqlTemplate.livenessProbeInitialDelaySeconds = livenessInitial;
            mysqlTemplate.livenessProbePeriodSeconds = livenessPeriod;
            mysqlTemplate.readinessProbeInitialDelaySeconds = readinessInitial;
            mysqlTemplate.readinessProbePeriodSeconds = readinessPeriod;
            return this;
        }

        // 副本配置构建器方法
        public Builder replicas(Integer replicas) {
            mysqlTemplate.replicas = replicas;
            return this;
        }

        // 高级配置构建器方法
        public Builder enableBinlog(Boolean enable) {
            mysqlTemplate.enableBinlog = enable;
            return this;
        }

        public Builder backup(Boolean enable, String schedule) {
            mysqlTemplate.backupEnabled = enable;
            mysqlTemplate.backupSchedule = schedule;
            return this;
        }

        public Builder extraEnvs(Map<String, String> extraEnvs) {
            mysqlTemplate.extraEnvs = extraEnvs;
            return this;
        }

        public Builder mysqlConfiguration(Map<String, String> mysqlConfiguration) {
            mysqlTemplate.mysqlConfiguration = mysqlConfiguration;
            return this;
        }

        public MysqlTemplate build() {
            return mysqlTemplate;
        }
    }
}
