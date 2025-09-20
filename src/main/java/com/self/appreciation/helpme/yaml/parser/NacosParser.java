// NacosParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.NacosTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class NacosParser {

    private static final String NACOS = "nacos";
    private static final Logger logger = LoggerFactory.getLogger(NacosParser.class);

    public static String parseTemplate(String project, NacosTemplate nacosTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 PersistentVolumeClaim（如果启用了持久化）
        if (nacosTemplate.getPersistenceEnabled()) {
            String pvcName = String.join("-", project, NACOS, "pvc");
            PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
            persistentVolumeClaim.setNamespace(project);
            persistentVolumeClaim.setRequestStorage(nacosTemplate.getStorageSize());
            persistentVolumeClaim.setStorageClassName(nacosTemplate.getStorageClassName());
            LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
            result.append(JacksonUtils.toYaml(pvcMap));
        }

        // 创建 Nacos 容器
        Container nacosContainer = new Container(NACOS, nacosTemplate.getImage());
        nacosContainer.setPorts(Arrays.asList(
            new ContainerPort("client", nacosTemplate.getClientPort()),
            new ContainerPort("server", nacosTemplate.getServerPort()),
            new ContainerPort("raft", nacosTemplate.getRaftPort())
        ));

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("MODE", nacosTemplate.getMode()));
        envVars.add(new EnvVar("PREFER_HOST_MODE", nacosTemplate.getPreferHostMode()));
        envVars.add(new EnvVar("NACOS_SERVER_IP", ""));
        envVars.add(new EnvVar("NACOS_APPLICATION_PORT", String.valueOf(nacosTemplate.getClientPort())));
        envVars.add(new EnvVar("NACOS_AUTH_ENABLE", nacosTemplate.getNacosAuthEnable()));
        envVars.add(new EnvVar("NACOS_AUTH_TOKEN", "SecretKey012345678901234567890123456789012345678901234567890123456789"));

        if (StringUtils.hasText(nacosTemplate.getServerAddr())) {
            envVars.add(new EnvVar("NACOS_SERVERS", nacosTemplate.getServerAddr()));
        }

        if (nacosTemplate.getEmbeddedStorage()) {
            envVars.add(new EnvVar("EMBEDDED_STORAGE", "embedded"));
        } else {
            envVars.add(new EnvVar("SPRING_DATASOURCE_PLATFORM", "mysql"));
            envVars.add(new EnvVar("MYSQL_SERVICE_HOST", nacosTemplate.getDbHost()));
            envVars.add(new EnvVar("MYSQL_SERVICE_PORT", String.valueOf(nacosTemplate.getDbPort())));
            envVars.add(new EnvVar("MYSQL_SERVICE_DB_NAME", nacosTemplate.getDbName()));
            envVars.add(new EnvVar("MYSQL_SERVICE_USER", nacosTemplate.getDbUser()));
            envVars.add(new EnvVar("MYSQL_SERVICE_PASSWORD", nacosTemplate.getDbPassword()));
        }

        // 添加额外的环境变量
        if (nacosTemplate.getExtraEnvs() != null) {
            nacosTemplate.getExtraEnvs().forEach((key, value) ->
                envVars.add(new EnvVar(key, value)));
        }

        nacosContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", nacosTemplate.getCpuRequest());
        requirements.putRequest("memory", nacosTemplate.getMemoryRequest());
        requirements.putLimit("cpu", nacosTemplate.getCpuLimit());
        requirements.putLimit("memory", nacosTemplate.getMemoryLimit());
        nacosContainer.setResources(requirements);

        // 设置卷和挂载点（如果启用了持久化）
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (nacosTemplate.getPersistenceEnabled()) {
            String volumeName = "nacos-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, NACOS, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);
            volumes.add(dataVolume);

            VolumeMount dataMount = new VolumeMount(volumeName, "/home/nacos/data");
            volumeMounts.add(dataMount);
            nacosContainer.setVolumeMounts(volumeMounts);
        }

        // 设置存活探针
        if (nacosTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/nacos/actuator/health");
            httpGetAction.setPort(String.valueOf(nacosTemplate.getClientPort()));
            livenessHandler.setHttpGet(httpGetAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(nacosTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(nacosTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(nacosTemplate.getLivenessProbeTimeoutSeconds());
            nacosContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (nacosTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/nacos/actuator/health");
            httpGetAction.setPort(String.valueOf(nacosTemplate.getClientPort()));
            readinessHandler.setHttpGet(httpGetAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(nacosTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(nacosTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(nacosTemplate.getReadinessProbeTimeoutSeconds());
            nacosContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, NACOS, "deployment");
        String appLabel = String.join("-", project, NACOS);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, Arrays.asList(nacosContainer))
                .namespace(project)
                .replicas(nacosTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        if (!volumes.isEmpty()) {
            deployment.setVolumes(volumes);
        }

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, NACOS, "service");
        List<ServicePort> servicePorts = new ArrayList<>();
        servicePorts.add(new ServicePort(nacosTemplate.getClientPort(), nacosTemplate.getClientPort()));
        servicePorts.add(new ServicePort(nacosTemplate.getServerPort(), nacosTemplate.getServerPort()));
        servicePorts.add(new ServicePort(nacosTemplate.getRaftPort(), nacosTemplate.getRaftPort()));

        Service service = new Service(serviceName, labels, servicePorts);
        service.setNamespace(project);
        service.setType(nacosTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("Nacos template parsed: {}", result);
        return result.toString();
    }
}
