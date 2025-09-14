package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class Namespace {
    // ObjectMeta
    private String name;                           // metadata.name
    private Map<String, String> labels;            // metadata.labels
    private Map<String, String> annotations;       // metadata.annotations

    // NamespaceSpec (通常为空，除非使用了特定的配额或限制)
    private Object spec;                           // spec (通常不需要)

    // NamespaceStatus (通常由 Kubernetes 自动设置)
    private String phase;                          // status.phase (Active, Terminating)

    public Namespace(String name) {
        this.name = name;
    }

    public Namespace(String name, Map<String, String> labels, Map<String, String> annotations) {
        this.name = name;
        this.labels = labels;
        this.annotations = annotations;
    }
}
