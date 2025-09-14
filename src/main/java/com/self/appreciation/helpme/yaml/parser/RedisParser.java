package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.RedisTemplate;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import java.util.*;

public class RedisParser {

    private static final String REDIS = "redis";

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RedisParser.class);

    public static String ParseTemplate(String project, RedisTemplate redisTemplate) throws Exception {
        // configmap
        String configmapName = String.join("-", project, REDIS, "configmap");
        ConfigMap configMap = new ConfigMap(configmapName);
        configMap.setNamespace(project);
        Map<String, String> configData = new HashMap<>();
        StringBuilder configBuilder = new StringBuilder();
        configBuilder.append("port ").append(redisTemplate.getContainerPort()).append("\n");
        if (StringUtils.hasText(redisTemplate.getRedisPassword())) {
            configBuilder.append("requirepass ").append(redisTemplate.getRedisPassword()).append("\n");
        }
        configBuilder.append("maxmemory ").append(redisTemplate.getMaxMemory()).append("\n");
        configBuilder.append("maxmemory-policy ").append(redisTemplate.getMaxMemoryPolicy()).append("\n");
        configBuilder.append("timeout ").append(redisTemplate.getTimeout()).append("\n");
        configBuilder.append("bind ").append(redisTemplate.getBind()).append("\n");
        // redis.conf
        configData.put("redis.conf", configBuilder.toString());
        configMap.setData(configData);
        LinkedHashMap<String, Object> configmapMap = ConfigMapParser.parseTemplate(configMap);
        String configmapYaml = JacksonUtils.toYaml(configmapMap);

        // volumes
        String redisConfVolumeName = "redis-conf";
        Volume redisConfVolume = new Volume(redisConfVolumeName);
        ConfigMapVolumeSource configMapSource = new ConfigMapVolumeSource();
        configMapSource.setName(configmapName);
        redisConfVolume.setConfigMap(configMapSource);
        List<Volume> volumes = List.of(redisConfVolume);

        // containers
        Container container = new Container(REDIS, redisTemplate.getImage());
        // container ports
        ContainerPort containerPort = new ContainerPort(redisTemplate.getContainerPort());
        container.setPorts(List.of());
        // envs
        List<EnvVar> envVars = new ArrayList<>();
        if (redisTemplate.getRedisPassword() != null) {
            envVars.add(new EnvVar("REDIS_PASSWORD", redisTemplate.getRedisPassword()));
        }
        if (redisTemplate.getMaxMemory() != null) {
            envVars.add(new EnvVar("REDIS_MAXMEMORY", redisTemplate.getMaxMemory()));
        }
        container.setEnv(envVars);

        // resources
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", redisTemplate.getCpuRequest());
        requirements.putRequest("memory", redisTemplate.getMemoryRequest());
        requirements.putLimit("cpu", redisTemplate.getCpuLimit());
        requirements.putLimit("memory", redisTemplate.getMemoryLimit());
        container.setResources(requirements);

        // volumeMounts
        String redisConfDir = "/etc/redis";
        VolumeMount redisConfMount = new VolumeMount(redisConfVolumeName, redisConfDir);
        List<VolumeMount> volumeMounts = List.of(redisConfMount);
        container.setVolumeMounts(volumeMounts);
        List<Container> containers = List.of(container);

        String appLabel = project + "-" + REDIS;
        Map<String, String> labels = Map.of("app", appLabel);

        //deployment
        String deployName = String.join("-", project, REDIS, "deployment");
        Deployment deployment = new Deployment.Builder(deployName, containers)
                .namespace(project)
                .volumes(volumes)
                .labels(labels)
                .build();
        LinkedHashMap<String, Object> deployMap = DeploymentParser.parseTemplate(deployment);
        String deployYaml = JacksonUtils.toYaml(deployMap);

        // service ports
        ServicePort redisServicePort = new ServicePort(redisTemplate.getContainerPort(), redisTemplate.getContainerPort());

        // service
        String serviceName = String.join("-", project, REDIS, "service");
        Service service = new Service(serviceName, labels, List.of(redisServicePort));
        service.setNamespace(project);
        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        String serviceYaml = JacksonUtils.toYaml(serviceMap);

        StringBuilder result = new StringBuilder();
        result.append(configmapYaml);
        result.append(deployYaml);
        result.append(serviceYaml);
        logger.info("redisTemplate: {}", result);

        return result.toString();
    }
}
