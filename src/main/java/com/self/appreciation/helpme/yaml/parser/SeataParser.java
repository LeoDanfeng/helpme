// SeataParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.SeataTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class SeataParser {

    private static final String SEATA = "seata";
    private static final Logger logger = LoggerFactory.getLogger(SeataParser.class);

    public static String parseTemplate(String project, SeataTemplate seataTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 PersistentVolumeClaim（如果启用了持久化）
        if (seataTemplate.getPersistenceEnabled()) {
            String pvcName = String.join("-", project, SEATA, "pvc");
            PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
            persistentVolumeClaim.setNamespace(project);
            persistentVolumeClaim.setRequestStorage(seataTemplate.getStorageSize());
            persistentVolumeClaim.setStorageClassName(seataTemplate.getStorageClassName());
            LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
            result.append(JacksonUtils.toYaml(pvcMap));
        }

        // 创建 Seata 容器
        Container seataContainer = new Container(SEATA, seataTemplate.getImage());
        seataContainer.setPorts(Arrays.asList(new ContainerPort("http", seataTemplate.getPort())));

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("SEATA_PORT", String.valueOf(seataTemplate.getPort())));
        envVars.add(new EnvVar("STORE_MODE", seataTemplate.getStoreMode()));

        // 数据库相关环境变量
        if ("db".equals(seataTemplate.getStoreMode())) {
            envVars.add(new EnvVar("DB_HOST", seataTemplate.getDbHost()));
            envVars.add(new EnvVar("DB_PORT", String.valueOf(seataTemplate.getDbPort())));
            envVars.add(new EnvVar("DB_NAME", seataTemplate.getDbName()));
            envVars.add(new EnvVar("DB_USER", seataTemplate.getDbUser()));
            envVars.add(new EnvVar("DB_PASSWORD", seataTemplate.getDbPassword()));
        }

        // 注册中心相关环境变量
        if (StringUtils.hasText(seataTemplate.getRegistryType())) {
            envVars.add(new EnvVar("REGISTRY_TYPE", seataTemplate.getRegistryType()));
        }

        if (StringUtils.hasText(seataTemplate.getRegistryNacosServerAddr())) {
            envVars.add(new EnvVar("REGISTRY_NACOS_SERVER_ADDR", seataTemplate.getRegistryNacosServerAddr()));
        }

        if (StringUtils.hasText(seataTemplate.getRegistryNacosGroup())) {
            envVars.add(new EnvVar("REGISTRY_NACOS_GROUP", seataTemplate.getRegistryNacosGroup()));
        }

        // 配置中心相关环境变量
        if (StringUtils.hasText(seataTemplate.getConfigType())) {
            envVars.add(new EnvVar("CONFIG_TYPE", seataTemplate.getConfigType()));
        }

        if (StringUtils.hasText(seataTemplate.getConfigNacosServerAddr())) {
            envVars.add(new EnvVar("CONFIG_NACOS_SERVER_ADDR", seataTemplate.getConfigNacosServerAddr()));
        }

        if (StringUtils.hasText(seataTemplate.getConfigNacosGroup())) {
            envVars.add(new EnvVar("CONFIG_NACOS_GROUP", seataTemplate.getConfigNacosGroup()));
        }

        // JVM 参数
        StringBuilder jvmOptions = new StringBuilder();
        if (StringUtils.hasText(seataTemplate.getJvmXms())) {
            jvmOptions.append("-Xms").append(seataTemplate.getJvmXms()).append(" ");
        }
        if (StringUtils.hasText(seataTemplate.getJvmXmx())) {
            jvmOptions.append("-Xmx").append(seataTemplate.getJvmXmx()).append(" ");
        }
        if (StringUtils.hasText(seataTemplate.getJvmXmn())) {
            jvmOptions.append("-Xmn").append(seataTemplate.getJvmXmn()).append(" ");
        }
        if (jvmOptions.length() > 0) {
            envVars.add(new EnvVar("JAVA_OPTS", jvmOptions.toString().trim()));
        }

        // 添加额外的环境变量
        if (seataTemplate.getExtraEnvs() != null) {
            seataTemplate.getExtraEnvs().forEach((key, value) ->
                envVars.add(new EnvVar(key, value)));
        }

        seataContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", seataTemplate.getCpuRequest());
        requirements.putRequest("memory", seataTemplate.getMemoryRequest());
        requirements.putLimit("cpu", seataTemplate.getCpuLimit());
        requirements.putLimit("memory", seataTemplate.getMemoryLimit());
        seataContainer.setResources(requirements);

        // 设置卷和挂载点（如果启用了持久化）
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (seataTemplate.getPersistenceEnabled()) {
            String volumeName = "seata-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, SEATA, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);
            volumes.add(dataVolume);

            VolumeMount dataMount = new VolumeMount(volumeName, "/seata-server/data");
            volumeMounts.add(dataMount);
            seataContainer.setVolumeMounts(volumeMounts);
        }

        // 设置存活探针
        if (seataTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            TCPSocketAction tcpSocketAction = new TCPSocketAction();
            tcpSocketAction.setPort(String.valueOf(seataTemplate.getPort()));
            livenessHandler.setTcpSocket(tcpSocketAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(seataTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(seataTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(seataTemplate.getLivenessProbeTimeoutSeconds());
            seataContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (seataTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/actuator/health");
            httpGetAction.setPort(String.valueOf(seataTemplate.getPort()));
            readinessHandler.setHttpGet(httpGetAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(seataTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(seataTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(seataTemplate.getReadinessProbeTimeoutSeconds());
            seataContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, SEATA, "deployment");
        String appLabel = String.join("-", project, SEATA);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, Arrays.asList(seataContainer))
                .namespace(project)
                .replicas(seataTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        if (!volumes.isEmpty()) {
            deployment.setVolumes(volumes);
        }

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, SEATA, "service");
        ServicePort servicePort = new ServicePort(seataTemplate.getPort(), seataTemplate.getPort());
        servicePort.setName("http");

        Service service = new Service(serviceName, labels, Arrays.asList(servicePort));
        service.setNamespace(project);
        service.setType(seataTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("Seata template parsed: {}", result);
        return result.toString();
    }
}
