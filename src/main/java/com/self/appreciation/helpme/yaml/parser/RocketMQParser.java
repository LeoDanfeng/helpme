// RocketMQParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RocketMQParser {

    private static final String ROCKETMQ = "rocketmq";
    private static final String NAMESRV = "namesrv";
    private static final String BROKER = "broker";
    private static final Logger logger = LoggerFactory.getLogger(RocketMQParser.class);

    public static String parseTemplate(String project, RocketMQTemplate rocketMQTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 Name Server PersistentVolumeClaim（如果启用了持久化）
        if (rocketMQTemplate.getPersistenceEnabled()) {
            String namesrvPvcName = String.join("-", project, ROCKETMQ, NAMESRV, "pvc");
            PersistentVolumeClaim namesrvPvc = new PersistentVolumeClaim(namesrvPvcName);
            namesrvPvc.setNamespace(project);
            namesrvPvc.setRequestStorage(rocketMQTemplate.getStorageSize());
            namesrvPvc.setStorageClassName(rocketMQTemplate.getStorageClassName());
            LinkedHashMap<String, Object> namesrvPvcMap = PersistentVolumeClaimParser.ParseTemplate(namesrvPvc);
            result.append(JacksonUtils.toYaml(namesrvPvcMap));

            String brokerPvcName = String.join("-", project, ROCKETMQ, BROKER, "pvc");
            PersistentVolumeClaim brokerPvc = new PersistentVolumeClaim(brokerPvcName);
            brokerPvc.setNamespace(project);
            brokerPvc.setRequestStorage(rocketMQTemplate.getStorageSize());
            brokerPvc.setStorageClassName(rocketMQTemplate.getStorageClassName());
            LinkedHashMap<String, Object> brokerPvcMap = PersistentVolumeClaimParser.ParseTemplate(brokerPvc);
            result.append(JacksonUtils.toYaml(brokerPvcMap));
        }

        // 创建 Name Server 容器
        Container namesrvContainer = new Container(NAMESRV, rocketMQTemplate.getNamesrvImage());
        namesrvContainer.setPorts(List.of(new ContainerPort("namesrv", rocketMQTemplate.getNamesrvPort())));

        // 设置 Name Server 环境变量
        List<EnvVar> namesrvEnvVars = new ArrayList<>();
        namesrvEnvVars.add(new EnvVar("JAVA_OPT", "-server -Xms512m -Xmx512m -Xmn256m"));
        namesrvContainer.setEnv(namesrvEnvVars);

        // 设置 Name Server 资源限制
        ResourceRequirements namesrvRequirements = new ResourceRequirements();
        namesrvRequirements.putRequest("cpu", rocketMQTemplate.getNamesrvCpuRequest());
        namesrvRequirements.putRequest("memory", rocketMQTemplate.getNamesrvMemoryRequest());
        namesrvRequirements.putLimit("cpu", rocketMQTemplate.getNamesrvCpuLimit());
        namesrvRequirements.putLimit("memory", rocketMQTemplate.getNamesrvMemoryLimit());
        namesrvContainer.setResources(namesrvRequirements);

        // 创建 Name Server Deployment
        String namesrvDeploymentName = String.join("-", project, ROCKETMQ, NAMESRV, "deployment");
        String namesrvAppLabel = String.join("-", project, ROCKETMQ, NAMESRV);
        Map<String, String> namesrvLabels = Map.of("app", namesrvAppLabel);

        Deployment namesrvDeployment = new Deployment.Builder(namesrvDeploymentName, List.of(namesrvContainer))
                .namespace(project)
                .replicas(rocketMQTemplate.getNamesrvReplicas())
                .labels(namesrvLabels)
                .templateLabels(namesrvLabels)
                .build();

        // 设置 Name Server 卷和挂载点（如果启用了持久化）
        if (rocketMQTemplate.getPersistenceEnabled()) {
            String volumeName = "namesrv-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                    String.join("-", project, ROCKETMQ, NAMESRV, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);

            VolumeMount dataMount = new VolumeMount(volumeName, "/home/rocketmq/store");
            namesrvContainer.setVolumeMounts(List.of(dataMount));
            namesrvDeployment.setVolumes(List.of(dataVolume));
        }

        LinkedHashMap<String, Object> namesrvDeploymentMap = DeploymentParser.parseTemplate(namesrvDeployment);
        result.append(JacksonUtils.toYaml(namesrvDeploymentMap));

        // 创建 Broker 容器
        Container brokerContainer = new Container(BROKER, rocketMQTemplate.getBrokerImage());
        brokerContainer.setPorts(List.of(
                new ContainerPort("broker", rocketMQTemplate.getBrokerPort()),
                new ContainerPort("broker-ha", rocketMQTemplate.getBrokerHaPort())
        ));

        // 设置 Broker 环境变量
        List<EnvVar> brokerEnvVars = new ArrayList<>();
        brokerEnvVars.add(new EnvVar("JAVA_OPT", "-server -Xms1g -Xmx1g -Xmn512m"));
        brokerEnvVars.add(new EnvVar("NAMESRV_ADDR", String.join("-", project, ROCKETMQ, NAMESRV, "service") + ":" + rocketMQTemplate.getNamesrvPort()));
        brokerContainer.setEnv(brokerEnvVars);

        // 设置 Broker 资源限制
        ResourceRequirements brokerRequirements = new ResourceRequirements();
        brokerRequirements.putRequest("cpu", rocketMQTemplate.getBrokerCpuRequest());
        brokerRequirements.putRequest("memory", rocketMQTemplate.getBrokerMemoryRequest());
        brokerRequirements.putLimit("cpu", rocketMQTemplate.getBrokerCpuLimit());
        brokerRequirements.putLimit("memory", rocketMQTemplate.getBrokerMemoryLimit());
        brokerContainer.setResources(brokerRequirements);

        // 创建 Broker Deployment
        String brokerDeploymentName = String.join("-", project, ROCKETMQ, BROKER, "deployment");
        String brokerAppLabel = String.join("-", project, ROCKETMQ, BROKER);
        Map<String, String> brokerLabels = Map.of("app", brokerAppLabel);

        Deployment brokerDeployment = new Deployment.Builder(brokerDeploymentName, List.of(brokerContainer))
                .namespace(project)
                .replicas(rocketMQTemplate.getBrokerReplicas())
                .labels(brokerLabels)
                .templateLabels(brokerLabels)
                .build();


        // 设置 Broker 卷和挂载点（如果启用了持久化）
        if (rocketMQTemplate.getPersistenceEnabled()) {
            String volumeName = "broker-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                    String.join("-", project, ROCKETMQ, BROKER, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);

            VolumeMount dataMount = new VolumeMount(volumeName, "/home/rocketmq/store");
            brokerContainer.setVolumeMounts(List.of(dataMount));

            brokerDeployment.setVolumes(List.of(dataVolume));
        }

        LinkedHashMap<String, Object> brokerDeploymentMap = DeploymentParser.parseTemplate(brokerDeployment);
        result.append(JacksonUtils.toYaml(brokerDeploymentMap));

        // 创建 Name Server Service
        String namesrvServiceName = String.join("-", project, ROCKETMQ, NAMESRV, "service");

        Service namesrvService = new Service(namesrvServiceName, namesrvLabels,
                List.of(new ServicePort(rocketMQTemplate.getNamesrvPort(), rocketMQTemplate.getNamesrvPort())));
        namesrvService.setNamespace(project);
        namesrvService.setType(rocketMQTemplate.getServiceType());

        LinkedHashMap<String, Object> namesrvServiceMap = ServiceParser.ParseTemplate(namesrvService);
        result.append(JacksonUtils.toYaml(namesrvServiceMap));

        // 创建 Broker Service
        String brokerServiceName = String.join("-", project, ROCKETMQ, BROKER, "service");

        List<ServicePort> brokerPorts = new ArrayList<>();
        brokerPorts.add(new ServicePort(rocketMQTemplate.getBrokerPort(), rocketMQTemplate.getBrokerPort()));
        brokerPorts.add(new ServicePort(rocketMQTemplate.getBrokerHaPort(), rocketMQTemplate.getBrokerHaPort()));

        Service brokerService = new Service(brokerServiceName, brokerLabels, brokerPorts);
        brokerService.setNamespace(project);
        brokerService.setType(rocketMQTemplate.getServiceType());

        LinkedHashMap<String, Object> brokerServiceMap = ServiceParser.ParseTemplate(brokerService);
        result.append(JacksonUtils.toYaml(brokerServiceMap));

        logger.info("RocketMQ template parsed: {}", result);
        return result.toString();
    }
}
