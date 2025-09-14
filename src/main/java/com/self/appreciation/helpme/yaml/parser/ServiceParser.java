package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.Service;
import com.self.appreciation.helpme.yaml.util.YamlPathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;


public class ServiceParser {

    private static final String SERVICE_TEMPLATE = "svc-template.yaml";

    public static LinkedHashMap<String, Object> ParseTemplate(Service svcTemplate) throws IOException {
        Path templatePath = TemplateParser.fileStorageLocation.resolve(SERVICE_TEMPLATE);
        LinkedHashMap<String, Object> service = JacksonUtils.readYamlFromFile(templatePath);
        YamlPathUtils.setValueByPath(service, "metadata.name", svcTemplate.getName());
        YamlPathUtils.setValueByPath(service, "metadata.namespace", svcTemplate.getNamespace());
        YamlPathUtils.setValueByPath(service, "spec.selector", svcTemplate.getSelector());
        YamlPathUtils.setValueByPath(service, "spec.ports", svcTemplate.getPorts());
        return service;
    }
}
