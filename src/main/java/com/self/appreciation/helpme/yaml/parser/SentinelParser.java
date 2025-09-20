// SentinelParser.java
package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.*;
import com.self.appreciation.helpme.yaml.template.SentinelTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SentinelParser {

    private static final String SENTINEL = "sentinel";
    private static final Logger logger = LoggerFactory.getLogger(SentinelParser.class);

    public static String parseTemplate(String project, SentinelTemplate sentinelTemplate) throws Exception {
        StringBuilder result = new StringBuilder();

        // 创建 Sentinel 容器
        Container sentinelContainer = new Container(SENTINEL, sentinelTemplate.getImage());
        sentinelContainer.setPorts(List.of(new ContainerPort("dashboard", sentinelTemplate.getDashboardPort())));

        // 设置环境变量
        List<EnvVar> envVars = new ArrayList<>();
        envVars.add(new EnvVar("SENTINEL_DASHBOARD_AUTH_USERNAME", sentinelTemplate.getSentinelAuthUsername()));
        envVars.add(new EnvVar("SENTINEL_DASHBOARD_AUTH_PASSWORD", sentinelTemplate.getSentinelAuthPassword()));
        envVars.add(new EnvVar("SERVER_PORT", String.valueOf(sentinelTemplate.getDashboardPort())));
        envVars.add(new EnvVar("PROJECT_NAME", sentinelTemplate.getProjectName()));
        envVars.add(new EnvVar("AUTH_ENABLED", sentinelTemplate.getAuthEnabled()));
        envVars.add(new EnvVar("SESSION_TIMEOUT", sentinelTemplate.getSessionTimeout()));
        sentinelContainer.setEnv(envVars);

        // 设置资源限制
        ResourceRequirements requirements = new ResourceRequirements();
        requirements.putRequest("cpu", sentinelTemplate.getCpuRequest());
        requirements.putRequest("memory", sentinelTemplate.getMemoryRequest());
        requirements.putLimit("cpu", sentinelTemplate.getCpuLimit());
        requirements.putLimit("memory", sentinelTemplate.getMemoryLimit());
        sentinelContainer.setResources(requirements);

        // 设置存活探针
        if (sentinelTemplate.getLivenessProbeInitialDelaySeconds() != null) {
            Probe livenessProbe = new Probe();
            Handler livenessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/version.label");
            httpGetAction.setPort(String.valueOf(sentinelTemplate.getDashboardPort()));
            livenessHandler.setHttpGet(httpGetAction);
            livenessProbe.setHandler(livenessHandler);
            livenessProbe.setInitialDelaySeconds(sentinelTemplate.getLivenessProbeInitialDelaySeconds());
            livenessProbe.setPeriodSeconds(sentinelTemplate.getLivenessProbePeriodSeconds());
            livenessProbe.setTimeoutSeconds(sentinelTemplate.getLivenessProbeTimeoutSeconds());
            sentinelContainer.setLivenessProbe(livenessProbe);
        }

        // 设置就绪探针
        if (sentinelTemplate.getReadinessProbeInitialDelaySeconds() != null) {
            Probe readinessProbe = new Probe();
            Handler readinessHandler = new Handler();
            HTTPGetAction httpGetAction = new HTTPGetAction();
            httpGetAction.setPath("/version.label");
            httpGetAction.setPort(String.valueOf(sentinelTemplate.getDashboardPort()));
            readinessHandler.setHttpGet(httpGetAction);
            readinessProbe.setHandler(readinessHandler);
            readinessProbe.setInitialDelaySeconds(sentinelTemplate.getReadinessProbeInitialDelaySeconds());
            readinessProbe.setPeriodSeconds(sentinelTemplate.getReadinessProbePeriodSeconds());
            readinessProbe.setTimeoutSeconds(sentinelTemplate.getReadinessProbeTimeoutSeconds());
            sentinelContainer.setReadinessProbe(readinessProbe);
        }

        // 创建 Deployment
        String deploymentName = String.join("-", project, SENTINEL, "deployment");
        String appLabel = String.join("-", project, SENTINEL);
        Map<String, String> labels = Map.of("app", appLabel);

        Deployment deployment = new Deployment.Builder(deploymentName, List.of(sentinelContainer))
                .namespace(project)
                .replicas(sentinelTemplate.getReplicas())
                .labels(labels)
                .templateLabels(labels)
                .build();

        LinkedHashMap<String, Object> deploymentMap = DeploymentParser.parseTemplate(deployment);
        result.append(JacksonUtils.toYaml(deploymentMap));

        // 创建 Service
        String serviceName = String.join("-", project, SENTINEL, "service");
        ServicePort servicePort = new ServicePort(sentinelTemplate.getDashboardPort(), sentinelTemplate.getDashboardPort());
        servicePort.setName("dashboard");

        Service service = new Service(serviceName, labels, List.of(servicePort));
        service.setNamespace(project);
        service.setType(sentinelTemplate.getServiceType());

        LinkedHashMap<String, Object> serviceMap = ServiceParser.ParseTemplate(service);
        result.append(JacksonUtils.toYaml(serviceMap));

        logger.info("Sentinel template parsed: {}", result);
        return result.toString();
    }
}
