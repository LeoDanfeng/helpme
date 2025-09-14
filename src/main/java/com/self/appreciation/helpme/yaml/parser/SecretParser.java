package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.Secret;
import com.self.appreciation.helpme.yaml.util.YamlPathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class SecretParser {
    private static final String SECRET_TEMPLATE = "secret-template.yaml";

    public static LinkedHashMap<String, Object> parseTemplate(Secret secretTemplate) throws IOException {
        Path templatePath = TemplateParser.fileStorageLocation.resolve(SECRET_TEMPLATE);
        LinkedHashMap<String, Object> secret = JacksonUtils.readYamlFromFile(templatePath);
        YamlPathUtils.setValueByPath(secret, "metadata.namespace", secretTemplate.getNamespace());
        YamlPathUtils.setValueByPath(secret, "metadata.name", secretTemplate.getName());
        YamlPathUtils.setValueByPath(secret, "data", secretTemplate.getData());
        return secret;
    }
}
