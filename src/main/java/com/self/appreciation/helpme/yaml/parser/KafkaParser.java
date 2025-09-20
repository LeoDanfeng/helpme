// KafkaParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.KafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class KafkaParser {

    private static final String KAFKA = "kafka";
    private static final Logger logger = LoggerFactory.getLogger(KafkaParser.class);

    public static String parseTemplate(String project, KafkaTemplate kafkaTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 PersistentVolumeClaim（如果启用了持久化）
        if (kafkaTemplate.getPersistenceEnabled()) {
            String pvcName = String.join("-", project, KAFKA, "pvc");
            PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
            persistentVolumeClaim.setNamespace(project);
            persistentVolumeClaim.setRequestStorage(kafkaTemplate.getStorageSize());
            persistentVolumeClaim.setStorageClassName(kafkaTemplate.getStorageClassName());
            LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
            result.append(JacksonUtils.toYaml(pvcMap));
        }

        // 创建 Kafka 容器
        Container kafkaContainer = new Container(KAFKA, kafkaTemplate.getImage());

        // 设置端口
        List<ContainerPort> ports = new ArrayList<>();
        ports.add(new ContainerPort("client", kafkaTemplate.getContainerPort()));
        if (kafkaTemplate.getExternalPort() != null) {
            ports.add(new ContainerPort("external", kafkaTemplate.getExternalPort()));
        }
        kafkaContainer.setPorts(ports);

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("KAFKA_CFG_PROCESS_ROLES", "broker"));
        envVars.add(new EnvVar("KAFKA_CFG_CONTROLLER_LISTENER_NAMES", "PLAINTEXT"));
        envVars.add(new EnvVar("KAFKA_CFG_LISTENERS", kafkaTemplate.getListeners()));
        envVars.add(new EnvVar("KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP", kafkaTemplate.getListenerSecurityProtocolMap()));
        envVars.add(new EnvVar("KAFKA_CFG_INTER_BROKER_LISTENER_NAME", kafkaTemplate.getInterBrokerListenerName()));
        envVars.add(new EnvVar("KAFKA_CFG_ZOOKEEPER_CONNECT", kafkaTemplate.getZookeeperConnect()));
        envVars.add(new EnvVar("KAFKA_CFG_LOG_RETENTION_HOURS", kafkaTemplate.getLogRetentionHours()));
        envVars.add(new EnvVar("KAFKA_CFG_LOG_SEGMENT_BYTES", kafkaTemplate.getLogSegmentBytes()));
        envVars.add(new EnvVar("KAFKA_CFG_NUM_PARTITIONS", kafkaTemplate.getNumPartitions()));
        envVars.add(new EnvVar("KAFKA_CFG_DEFAULT_REPLICATION_FACTOR", kafkaTemplate.getDefaultReplicationFactor()));
        envVars.add(new EnvVar("KAFKA_CFG_OFFSETS_TOPIC_REPLICATION_FACTOR", kafkaTemplate.getOffsetsTopicReplicationFactor()));
        envVars.add(new EnvVar("KAFKA_CFG_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", kafkaTemplate.getTransactionStateLogReplicationFactor()));
        envVars.add(new EnvVar("KAFKA_CFG_TRANSACTION_STATE_LOG_MIN_ISR", kafkaTemplate.getTransactionStateLogMinIsr()));

        if (StringUtils.hasText(kafkaTemplate.getAdvertisedListeners())) {
            envVars.add(new EnvVar("KAFKA_CFG_ADVERTISED_LISTENERS", kafkaTemplate.getAdvertisedListeners()));
        }

        // 添加额外的环境变量
        if (kafkaTemplate.getExtraEnvs() != null) {
            kafkaTemplate.getExtraEnvs().forEach((key, value) ->
                envVars.add(new EnvVar(key, value)));
        }

        kafkaContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", kafkaTemplate.getCpuRequest());
        requirements.putRequest("memory", kafkaTemplate.getMemoryRequest());
        requirements.putLimit("cpu", kafkaTemplate.getCpuLimit());
        requirements.putLimit("memory", kafkaTemplate.getMemoryLimit());
        kafkaContainer.setResources(requirements);

        // 设置卷和挂载点（如果启用了持久化）
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (kafkaTemplate.getPersistenceEnabled()) {
            String volumeName = "kafka-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, KAFKA, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);
            volumes.add(dataVolume);

            VolumeMount dataMount = new VolumeMount(volumeName, "/bitnami/kafka");
            volumeMounts.add(dataMount);
            kafkaContainer.setVolumeMounts(volumeMounts);
        }

        // 设置存活探针
        if (kafkaTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            TCPSocketAction tcpSocketAction = new TCPSocketAction();
            tcpSocketAction.setPort(String.valueOf(kafkaTemplate.getContainerPort()));
            livenessHandler.setTcpSocket(tcpSocketAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(kafkaTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(kafkaTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(kafkaTemplate.getLivenessProbeTimeoutSeconds());
            kafkaContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (kafkaTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            TCPSocketAction tcpSocketAction = new TCPSocketAction();
            tcpSocketAction.setPort(String.valueOf(kafkaTemplate.getContainerPort()));
            readinessHandler.setTcpSocket(tcpSocketAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(kafkaTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(kafkaTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(kafkaTemplate.getReadinessProbeTimeoutSeconds());
            kafkaContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, KAFKA, "deployment");
        String appLabel = String.join("-", project, KAFKA);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, List.of(kafkaContainer))
                .namespace(project)
                .replicas(kafkaTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        if (!volumes.isEmpty()) {
            deployment.setVolumes(volumes);
        }

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, KAFKA, "service");
        List<ServicePort> servicePorts = new ArrayList<>();
        servicePorts.add(new ServicePort(kafkaTemplate.getContainerPort(), kafkaTemplate.getContainerPort()));
        if (kafkaTemplate.getExternalPort() != null) {
            servicePorts.add(new ServicePort(kafkaTemplate.getExternalPort(), kafkaTemplate.getExternalPort()));
        }

        Service service = new Service(serviceName, labels, servicePorts);
        service.setNamespace(project);
        service.setType(kafkaTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("Kafka template parsed: {}", result);
        return result.toString();
    }
}
