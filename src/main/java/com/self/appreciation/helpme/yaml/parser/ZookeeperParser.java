// ZookeeperParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.ZookeeperTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ZookeeperParser {

    private static final String ZOOKEEPER = "zookeeper";
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperParser.class);

    public static String parseTemplate(String project, ZookeeperTemplate zookeeperTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 PersistentVolumeClaim（如果启用了持久化）
        if (zookeeperTemplate.getPersistenceEnabled()) {
            String pvcName = String.join("-", project, ZOOKEEPER, "pvc");
            PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
            persistentVolumeClaim.setNamespace(project);
            persistentVolumeClaim.setRequestStorage(zookeeperTemplate.getStorageSize());
            persistentVolumeClaim.setStorageClassName(zookeeperTemplate.getStorageClassName());
            LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
            result.append(JacksonUtils.toYaml(pvcMap));
        }

        // 创建 Zookeeper 容器
        Container zookeeperContainer = new Container(ZOOKEEPER, zookeeperTemplate.getImage());
        zookeeperContainer.setPorts(Arrays.asList(
            new ContainerPort("client", zookeeperTemplate.getClientPort()),
            new ContainerPort("server", zookeeperTemplate.getServerPort()),
            new ContainerPort("election", zookeeperTemplate.getElectionPort())
        ));

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("ZOO_TICK_TIME", zookeeperTemplate.getTickTime()));
        envVars.add(new EnvVar("ZOO_INIT_LIMIT", zookeeperTemplate.getInitLimit()));
        envVars.add(new EnvVar("ZOO_SYNC_LIMIT", zookeeperTemplate.getSyncLimit()));
        envVars.add(new EnvVar("ZOO_MAX_CLIENT_CNXNS", zookeeperTemplate.getMaxClientCnxns()));
        envVars.add(new EnvVar("ZOO_AUTOPURGE_SNAP_RETAIN_COUNT", zookeeperTemplate.getSnapRetainCount()));
        envVars.add(new EnvVar("ZOO_AUTOPURGE_PURGE_INTERVAL", zookeeperTemplate.getPurgeInterval()));
        envVars.add(new EnvVar("ZOO_SERVERS", zookeeperTemplate.getServers()));
        zookeeperContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", zookeeperTemplate.getCpuRequest());
        requirements.putRequest("memory", zookeeperTemplate.getMemoryRequest());
        requirements.putLimit("cpu", zookeeperTemplate.getCpuLimit());
        requirements.putLimit("memory", zookeeperTemplate.getMemoryLimit());
        zookeeperContainer.setResources(requirements);

        // 设置卷和挂载点（如果启用了持久化）
        List<Volume> volumes = new ArrayList<>();
        List<VolumeMount> volumeMounts = new ArrayList<>();

        if (zookeeperTemplate.getPersistenceEnabled()) {
            String volumeName = "zookeeper-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, ZOOKEEPER, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);
            volumes.add(dataVolume);

            VolumeMount dataMount = new VolumeMount(volumeName, "/data");
            VolumeMount datalogMount = new VolumeMount(volumeName, "/datalog");
            volumeMounts.add(dataMount);
            volumeMounts.add(datalogMount);
            zookeeperContainer.setVolumeMounts(volumeMounts);
        }

        // 设置存活探针
        if (zookeeperTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            ExecAction execAction = new ExecAction();
            execAction.setCommand(Arrays.asList("sh", "-c", "echo ruok | nc localhost 2181 | grep imok"));
            livenessHandler.setExec(execAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(zookeeperTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(zookeeperTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(zookeeperTemplate.getLivenessProbeTimeoutSeconds());
            zookeeperContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (zookeeperTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            ExecAction execAction = new ExecAction();
            execAction.setCommand(Arrays.asList("sh", "-c", "echo ruok | nc localhost 2181 | grep imok"));
            readinessHandler.setExec(execAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(zookeeperTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(zookeeperTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(zookeeperTemplate.getReadinessProbeTimeoutSeconds());
            zookeeperContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, ZOOKEEPER, "deployment");
        String appLabel = String.join("-", project, ZOOKEEPER);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, Arrays.asList(zookeeperContainer))
                .namespace(project)
                .replicas(zookeeperTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        if (!volumes.isEmpty()) {
            deployment.setVolumes(volumes);
        }

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, ZOOKEEPER, "service");
        List<ServicePort> servicePorts = new ArrayList<>();
        servicePorts.add(new ServicePort(zookeeperTemplate.getClientPort(), zookeeperTemplate.getClientPort()));
        servicePorts.add(new ServicePort(zookeeperTemplate.getServerPort(), zookeeperTemplate.getServerPort()));
        servicePorts.add(new ServicePort(zookeeperTemplate.getElectionPort(), zookeeperTemplate.getElectionPort()));

        Service service = new Service(serviceName, labels, servicePorts);
        service.setNamespace(project);
        service.setType(zookeeperTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("Zookeeper template parsed: {}", result);
        return result.toString();
    }
}
