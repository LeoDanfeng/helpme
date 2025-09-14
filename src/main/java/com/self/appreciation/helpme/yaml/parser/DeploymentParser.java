package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.Deployment;
import com.self.appreciation.helpme.yaml.util.YamlPathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class DeploymentParser {

    private static final String DEPLOYMENT_TEMPLATE = "deploy-template.yaml";

    public static LinkedHashMap<String, Object> parseTemplate(Deployment deployTemplate) throws IOException {
        Path templatePath = TemplateParser.fileStorageLocation.resolve(DEPLOYMENT_TEMPLATE);
        LinkedHashMap<String, Object> deployment = JacksonUtils.readYamlFromFile(templatePath);
        YamlPathUtils.setValueByPath(deployment, "metadata.name", deployTemplate.getName());
        YamlPathUtils.setValueByPath(deployment, "metadata.namespace", deployTemplate.getNamespace());
        YamlPathUtils.setValueByPath(deployment, "spec.replicas", deployTemplate.getReplicas());
        YamlPathUtils.setValueByPath(deployment, "metadata.labels", deployTemplate.getLabels());
//        YamlPathUtils.setValueByPath(deployment, "spec.strategy.type", "Recreate");
        YamlPathUtils.setValueByPath(deployment, "spec.selector.matchLabels", deployTemplate.getLabels());
        YamlPathUtils.setValueByPath(deployment, "spec.template.metadata.labels", deployTemplate.getLabels());
        YamlPathUtils.setValueByPath(deployment, "spec.template.spec.containers", deployTemplate.getContainers());
        YamlPathUtils.setValueByPath(deployment, "spec.template.spec.volumes", deployTemplate.getVolumes());
        return deployment;
    }
}
