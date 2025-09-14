package com.self.appreciation.helpme.yaml.parser;

import com.self.appreciation.helpme.util.JacksonUtils;
import com.self.appreciation.helpme.yaml.model.PersistentVolumeClaim;
import com.self.appreciation.helpme.yaml.util.YamlPathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;


public class PersistentVolumeClaimParser {
    private static final String PVC_TEMPLATE = "pvc-template.yaml";

    public static LinkedHashMap<String, Object> ParseTemplate(PersistentVolumeClaim persistentVolumeClaim) throws IOException {
        Path templatePath = TemplateParser.fileStorageLocation.resolve(PVC_TEMPLATE);
        LinkedHashMap<String, Object> pvc = JacksonUtils.readYamlFromFile(templatePath);
        YamlPathUtils.setValueByPath(pvc, "metadata.namespace", persistentVolumeClaim.getNamespace());
        YamlPathUtils.setValueByPath(pvc, "metadata.name", persistentVolumeClaim.getName());
        YamlPathUtils.setValueByPath(pvc, "spec.storageClassName", persistentVolumeClaim.getStorageClassName());
        YamlPathUtils.setValueByPath(pvc, "spec.accessModes", persistentVolumeClaim.getAccessModes());
        YamlPathUtils.setValueByPath(pvc, "spec.resources.requests.storage", persistentVolumeClaim.getRequestStorage());
//        YamlPathUtils.setValueByPath(pvc, "spec.resources.limits.storage", persistentVolumeClaim.getLimitStorage()); //  PVC 不应该有 limits.storage
        return pvc;
    }
}
