package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ConfigMap {
    // ObjectMeta
    private String name;                           // metadata.name
    private String namespace;                      // metadata.namespace
    private Map<String, String> labels;            // metadata.labels
    private Map<String, String> annotations;       // metadata.annotations

    // ConfigMapSpec
    private Map<String, String> data;              // data (key-value pairs)
    private Map<String, String> binaryData;        // binaryData (base64 encoded binary data)

    public ConfigMap(String name) {
        this.name = name;
    }
}
