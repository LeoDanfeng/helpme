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
        namesrvContainer.setPorts(Arrays.asList(new ContainerPort("namesrv", rocketMQTemplate.getNamesrvPort())));

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

        // 设置 Name Server 卷和挂载点（如果启用了持久化）
        if (rocketMQTemplate.getPersistenceEnabled()) {
            String volumeName = "namesrv-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, ROCKETMQ, NAMESRV, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);

            VolumeMount dataMount = new VolumeMount(volumeName, "/home/rocketmq/store");
            namesrvContainer.setVolumeMounts(Arrays.asList(dataMount));
        }

        // 创建 Broker 容器
        Container brokerContainer = new Container(BROKER, rocketMQTemplate.getBrokerImage());
        brokerContainer.setPorts(Arrays.asList(
            new ContainerPort("broker", rocketMQTemplate.getBrokerPort()),
            new ContainerPort("broker-ha", rocketMQTemplate.getBrokerHaPort())
        ));

        // 设置 Broker 环境变量
        List<EnvVar> brokerEnvVars = new ArrayList<>();
        brokerEnvVars.add(new EnvVar("JAVA_OPT", "-server -Xms1g -Xmx1g -Xmn512m"));
        brokerEnvVars.add(new EnvVar("NAMESRV_ADDR", "localhost:" + rocketMQTemplate.getNamesrvPort()));
        brokerContainer.setEnv(brokerEnvVars);

        // 设置 Broker 资源限制
        ResourceRequirements brokerRequirements = new ResourceRequirements();
        brokerRequirements.putRequest("cpu", rocketMQTemplate.getBrokerCpuRequest());
        brokerRequirements.putRequest("memory", rocketMQTemplate.getBrokerMemoryRequest());
        brokerRequirements.putLimit("cpu", rocketMQTemplate.getBrokerCpuLimit());
        brokerRequirements.putLimit("memory", rocketMQTemplate.getBrokerMemoryLimit());
        brokerContainer.setResources(brokerRequirements);

        // 设置 Broker 卷和挂载点（如果启用了持久化）
        if (rocketMQTemplate.getPersistenceEnabled()) {
            String volumeName = "broker-data";
            Volume dataVolume = new Volume(volumeName);
            PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(
                String.join("-", project, ROCKETMQ, BROKER, "pvc"));
            dataVolume.setPersistentVolumeClaim(pvcVolumeSource);

            VolumeMount dataMount = new VolumeMount(volumeName, "/home/rocketmq/store");
            brokerContainer.setVolumeMounts(Arrays.asList(dataMount));
        }

        // 创建 Name Server Service
        String namesrvServiceName = String.join("-", project, ROCKETMQ, NAMESRV, "service");
        String namesrvAppLabel = String.join("-", project, ROCKETMQ, NAMESRV);
        Map<String, String> namesrvLabels = Map.of("app", namesrvAppLabel);

        Service namesrvService = new Service(namesrvServiceName, namesrvLabels,
            Arrays.asList(new ServicePort(rocketMQTemplate.getNamesrvPort(), rocketMQTemplate.getNamesrvPort())));
        namesrvService.setNamespace(project);
        namesrvService.setType(rocketMQTemplate.getServiceType());

        LinkedHashMap<String, Object> namesrvServiceMap = ServiceParser.ParseTemplate(namesrvService);
        result.append(JacksonUtils.toYaml(namesrvServiceMap));

        // 创建 Broker Service
        String brokerServiceName = String.join("-", project, ROCKETMQ, BROKER, "service");
        String brokerAppLabel = String.join("-", project, ROCKETMQ, BROKER);
        Map<String, String> brokerLabels = Map.of("app", brokerAppLabel);

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
