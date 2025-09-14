package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.Namespace;
import com.self.appreciation.helpme.yaml.model.Secret;
import com.self.appreciation.helpme.yaml.util.YamlPathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class NamespaceParser {

    private static final String NAMESPACE_TEMPLATE = "namespace-template.yaml";

    public static LinkedHashMap<String, Object> parseTemplate(Namespace namespaceTemplate) throws IOException {
        Path templatePath = TemplateParser.fileStorageLocation.resolve(NAMESPACE_TEMPLATE);
        LinkedHashMap<String, Object> secret = JacksonUtils.readYamlFromFile(templatePath);
        YamlPathUtils.setValueByPath(secret, "metadata.name", namespaceTemplate.getName());
        YamlPathUtils.setValueByPath(secret, "metadata.labels", namespaceTemplate.getLabels());
        YamlPathUtils.setValueByPath(secret, "metadata.annotations", namespaceTemplate.getAnnotations());
        return secret;
    }


}
