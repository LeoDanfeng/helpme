// ConsulParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.ConsulTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ConsulParser {

    private static final String CONSUL = "consul";
    private static final Logger logger = LoggerFactory.getLogger(ConsulParser.class);

    public static String parseTemplate(String project, ConsulTemplate consulTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 PersistentVolumeClaim（如果启用了持久化）
        if (consulTemplate.getPersistenceEnabled()) {
            String pvcName = String.join("-", project, CONSUL, "pvc");
            PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
            persistentVolumeClaim.setNamespace(project);
            persistentVolumeClaim.setRequestStorage(consulTemplate.getStorageSize());
            persistentVolumeClaim.setStorageClassName(consulTemplate.getStorageClassName());
            LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
            result.append(JacksonUtils.toYaml(pvcMap));
        }

        // 创建 Consul 容器
        Container consulContainer = new Container(CONSUL, consulTemplate.getImage());

        // 设置端口
        List<ContainerPort> ports = new ArrayList<>();
        ports.add(new ContainerPort("server", consulTemplate.getServerPort()));
        ports.add(new ContainerPort("serf-lan", consulTemplate.getSerfLanPort()));
        ports.add(new ContainerPort("serf-wan", consulTemplate.getSerfWanPort()));
        ports.add(new ContainerPort("client", consulTemplate.getClientPort()));
        ports.add(new ContainerPort("dns", consulTemplate.getDnsPort()));
        consulContainer.setPorts(ports);

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("CONSUL_BIND_INTERFACE", "eth0"));
        envVars.add(new EnvVar("CONSUL_CLIENT_INTERFACE", "eth0"));
        envVars.add(new EnvVar("CONSUL_DATAcenter", consulTemplate.getDatacenter()));
        envVars.add(new EnvVar("CONSUL_DOMAIN", consulTemplate.getDomain()));
        envVars.add(new EnvVar("CONSUL_SERVER", consulTemplate.getServer().toString()));
        envVars.add(new EnvVar("CONSUL_BOOTSTRAP_EXPECT", consulTemplate.getBootstrapExpect()));
        envVars.add(new EnvVar("CONSUL_UI", consulTemplate.getUi()));

        if (consulTemplate.getRetryJoin() != null && !consulTemplate.getRetryJoin().isEmpty()) {
            envVars.add(new EnvVar("CONSUL_RETRY_JOIN", consulTemplate.getRetryJoin()));
        }

        consulContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", consulTemplate.getCpuRequest());
        requirements.putRequest("memory", consulTemplate.getMemoryRequest());
        requirements.putLimit("cpu", consulTemplate.getCpuLimit());
        requirements.putLimit("memory", consulTemplate.getMemoryLimit());
        consulContainer.setResources(requirements);

        // 设置卷和挂载点（如果启用了持久化）
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (consulTemplate.getPersistenceEnabled()) {
            String volumeName = "consul-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, CONSUL, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);
            volumes.add(dataVolume);

            VolumeMount dataMount = new VolumeMount(volumeName, "/consul/data");
            volumeMounts.add(dataMount);
            consulContainer.setVolumeMounts(volumeMounts);
        }

        // 设置存活探针
        if (consulTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/v1/status/leader");
            httpGetAction.setPort(String.valueOf(consulTemplate.getClientPort()));
            livenessHandler.setHttpGet(httpGetAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(consulTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(consulTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(consulTemplate.getLivenessProbeTimeoutSeconds());
            consulContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (consulTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/v1/status/leader");
            httpGetAction.setPort(String.valueOf(consulTemplate.getClientPort()));
            readinessHandler.setHttpGet(httpGetAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(consulTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(consulTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(consulTemplate.getReadinessProbeTimeoutSeconds());
            consulContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, CONSUL, "deployment");
        String appLabel = String.join("-", project, CONSUL);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, List.of(consulContainer))
                .namespace(project)
                .replicas(consulTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        if (!volumes.isEmpty()) {
            deployment.setVolumes(volumes);
        }

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, CONSUL, "service");
        List<ServicePort> servicePorts = new ArrayList<>();
        servicePorts.add(new ServicePort(consulTemplate.getServerPort(), consulTemplate.getServerPort()));
        servicePorts.add(new ServicePort(consulTemplate.getSerfLanPort(), consulTemplate.getSerfLanPort()));
        servicePorts.add(new ServicePort(consulTemplate.getSerfWanPort(), consulTemplate.getSerfWanPort()));
        servicePorts.add(new ServicePort(consulTemplate.getClientPort(), consulTemplate.getClientPort()));
        servicePorts.add(new ServicePort(consulTemplate.getDnsPort(), consulTemplate.getDnsPort()));

        Service service = new Service(serviceName, labels, servicePorts);
        service.setNamespace(project);
        service.setType(consulTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("Consul template parsed: {}", result);
        return result.toString();
    }
}
