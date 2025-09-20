// RabbitMQParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.RabbitMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class RabbitMQParser {

    private static final String RABBITMQ = "rabbitmq";
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQParser.class);

    public static String parseTemplate(String project, RabbitMQTemplate rabbitMQTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 PersistentVolumeClaim（如果启用了持久化）
        if (rabbitMQTemplate.getPersistenceEnabled()) {
            String pvcName = String.join("-", project, RABBITMQ, "pvc");
            PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
            persistentVolumeClaim.setNamespace(project);
            persistentVolumeClaim.setRequestStorage(rabbitMQTemplate.getStorageSize());
            persistentVolumeClaim.setStorageClassName(rabbitMQTemplate.getStorageClassName());
            LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
            result.append(JacksonUtils.toYaml(pvcMap));
        }

        // 创建 RabbitMQ 容器
        Container rabbitMQContainer = new Container(RABBITMQ, rabbitMQTemplate.getImage());

        // 设置端口
        List<ContainerPort> ports = new ArrayList<>();
        ports.add(new ContainerPort("amqp", rabbitMQTemplate.getAmqpPort()));
        ports.add(new ContainerPort("management", rabbitMQTemplate.getManagementPort()));
        rabbitMQContainer.setPorts(ports);

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("RABBITMQ_DEFAULT_USER", rabbitMQTemplate.getRabbitmqUser()));
        envVars.add(new EnvVar("RABBITMQ_DEFAULT_PASS", rabbitMQTemplate.getRabbitmqPassword()));
        envVars.add(new EnvVar("RABBITMQ_ERLANG_COOKIE", rabbitMQTemplate.getRabbitmqErlangCookie()));
        envVars.add(new EnvVar("RABBITMQ_VM_MEMORY_HIGH_WATERMARK", rabbitMQTemplate.getVmMemoryHighWatermark()));

        if (rabbitMQTemplate.getClusteringEnabled()) {
            envVars.add(new EnvVar("RABBITMQ_CLUSTER_PARTITION_HANDLING", "autoheal"));
        }

        // 添加额外的环境变量
        if (rabbitMQTemplate.getExtraEnvs() != null) {
            rabbitMQTemplate.getExtraEnvs().forEach((key, value) ->
                envVars.add(new EnvVar(key, value)));
        }

        rabbitMQContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", rabbitMQTemplate.getCpuRequest());
        requirements.putRequest("memory", rabbitMQTemplate.getMemoryRequest());
        requirements.putLimit("cpu", rabbitMQTemplate.getCpuLimit());
        requirements.putLimit("memory", rabbitMQTemplate.getMemoryLimit());
        rabbitMQContainer.setResources(requirements);

        // 设置卷和挂载点（如果启用了持久化）
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (rabbitMQTemplate.getPersistenceEnabled()) {
            String volumeName = "rabbitmq-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, RABBITMQ, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);
            volumes.add(dataVolume);

            VolumeMount dataMount = new VolumeMount(volumeName, "/var/lib/rabbitmq/mnesia");
            volumeMounts.add(dataMount);
            rabbitMQContainer.setVolumeMounts(volumeMounts);
        }

        // 设置存活探针
        if (rabbitMQTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            ExecAction execAction = new ExecAction();
            execAction.setCommand(List.of("rabbitmq-diagnostics", "ping"));
            livenessHandler.setExec(execAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(rabbitMQTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(rabbitMQTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(rabbitMQTemplate.getLivenessProbeTimeoutSeconds());
            rabbitMQContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (rabbitMQTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            ExecAction execAction = new ExecAction();
            execAction.setCommand(List.of("rabbitmq-diagnostics", "check_running"));
            readinessHandler.setExec(execAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(rabbitMQTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(rabbitMQTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(rabbitMQTemplate.getReadinessProbeTimeoutSeconds());
            rabbitMQContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, RABBITMQ, "deployment");
        String appLabel = String.join("-", project, RABBITMQ);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, List.of(rabbitMQContainer))
                .namespace(project)
                .replicas(rabbitMQTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        if (!volumes.isEmpty()) {
            deployment.setVolumes(volumes);
        }

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, RABBITMQ, "service");
        List<ServicePort> servicePorts = new ArrayList<>();
        servicePorts.add(new ServicePort(rabbitMQTemplate.getAmqpPort(), rabbitMQTemplate.getAmqpPort()));
        servicePorts.add(new ServicePort(rabbitMQTemplate.getManagementPort(), rabbitMQTemplate.getManagementPort()));

        Service service = new Service(serviceName, labels, servicePorts);
        service.setNamespace(project);
        service.setType(rabbitMQTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("RabbitMQ template parsed: {}", result);
        return result.toString();
    }
}
