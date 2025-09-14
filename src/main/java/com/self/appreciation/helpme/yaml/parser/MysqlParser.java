package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.model.Deployment;
import com.self.appreciation.helpme.yaml.template.MysqlTemplate;
import com.self.appreciation.helpme.yaml.model.PersistentVolumeClaim;
import com.self.appreciation.helpme.yaml.model.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MysqlParser {

    private static final String MYSQL = "mysql";
    private static final Logger logger = LoggerFactory.getLogger(MysqlParser.class);

    public static String ParseTemplate(String project, MysqlTemplate mysqlTemplate) throws Exception {

        // persistentVolumeClaim
        String pvcName = String.join("-", project, MYSQL, "pvc");
        PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim(pvcName);
        persistentVolumeClaim.setNamespace(project);
        LinkedHashMap<String, Object> pvcMap = PersistentVolumeClaimParser.ParseTemplate(persistentVolumeClaim);
        String volumeYaml = JacksonUtils.toYaml(pvcMap);

        Container mysqlContainer = new Container(MYSQL, mysqlTemplate.getImage());
        ContainerPort mysqlPort = new ContainerPort(mysqlTemplate.getContainerPort());
        mysqlContainer.setPorts(List.of(mysqlPort));

        // envs
        List<EnvVar> envs = new ArrayList<>();
        envs.add(new EnvVar("MYSQL_ROOT_PASSWORD", mysqlTemplate.getMysqlRootPassword()));
        if (StringUtils.hasText(mysqlTemplate.getMysqlUser()) && StringUtils.hasText(mysqlTemplate.getMysqlPassword())) {
            envs.add(new EnvVar("MYSQL_USER", mysqlTemplate.getMysqlUser()));
            envs.add(new EnvVar("MYSQL_PASSWORD", mysqlTemplate.getMysqlPassword()));
        }
        if (StringUtils.hasText(mysqlTemplate.getMysqlDatabase())) {
            envs.add(new EnvVar("MYSQL_DATABASE", mysqlTemplate.getMysqlDatabase()));
        }
        mysqlContainer.setEnv(envs);

        // resources
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", mysqlTemplate.getCpuRequest());
        requirements.putRequest("memory", mysqlTemplate.getMemoryRequest());
        requirements.putLimit("cpu", mysqlTemplate.getCpuLimit());
        requirements.putLimit("memory", mysqlTemplate.getMemoryLimit());
        mysqlContainer.setResources(requirements);

        // volumes
        String mysqlDataVolumeName = "mysql-data";
        Volume mysqlDataVolume = new Volume(mysqlDataVolumeName);
        PersistentVolumeClaimVolumeSource pvcVolumeSource = new PersistentVolumeClaimVolumeSource(pvcName);
        mysqlDataVolume.setPersistentVolumeClaim(pvcVolumeSource);
        List<Volume> volumes = List.of(mysqlDataVolume);

        // volumeMounts
        List<VolumeMount> volumeMounts = new ArrayList<>();
        String mysqlDataDir = "/var/lib/mysql";
        VolumeMount mysqlDataMount = new VolumeMount(mysqlDataVolumeName, mysqlDataDir);
        volumeMounts.add(mysqlDataMount);
        mysqlContainer.setVolumeMounts(volumeMounts);


        //deployment
        String deploymentName = String.join("-", project, MYSQL, "deployment");

        // labels
        String appLabel = String.join("-", project, MYSQL);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, List.of(mysqlContainer))
                .namespace(project)
                .volumes(volumes)
                .labels(labels)
                .build();
        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        String deployYaml = JacksonUtils.toYaml(deploymentMap);

        // ports
        ServicePort mysqlServicePort = new ServicePort(3306, 3306);

        // service
        String svcName = String.join("-", project, MYSQL, "service");
        Service service = new Service(svcName, labels, List.of(mysqlServicePort));
        service.setNamespace(project);
        service.setClusterIP("None");
        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        String serviceYaml = JacksonUtils.toYaml(serviceMap);

        StringBuilder result = new StringBuilder();
        result.append(volumeYaml);
        result.append(deployYaml);
        result.append(serviceYaml);

        logger.info("MysqlTemplate: {}", result);

        return result.toString();
    }
}
