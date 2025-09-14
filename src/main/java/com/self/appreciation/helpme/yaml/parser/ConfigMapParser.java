package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.ConfigMap;
import com.self.appreciation.helpme.yaml.util.YamlPathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class ConfigMapParser {
    private static final String CONFIGMAP_TEMPLATE = "configmap-template.yaml";

    public static LinkedHashMap<String, Object> parseTemplate(ConfigMap configmapTemplate) throws IOException {
        Path templatePath = TemplateParser.fileStorageLocation.resolve(CONFIGMAP_TEMPLATE);
        LinkedHashMap<String, Object> configmap = JacksonUtils.readYamlFromFile(templatePath);
        YamlPathUtils.setValueByPath(configmap, "metadata.namespace", configmapTemplate.getNamespace());
        YamlPathUtils.setValueByPath(configmap, "metadata.name", configmapTemplate.getName());
        YamlPathUtils.setValueByPath(configmap, "data", configmapTemplate.getData());
        YamlPathUtils.setValueByPath(configmap, "metadata.labels", configmapTemplate.getLabels());
        return configmap;
    }
}
