package com.self.appreciation.helpme.yaml.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class Deployment {
    // ObjectMeta
    private String name;                           // metadata.name
    private Map<String, String> labels;            // metadata.labels
    private Map<String, String> annotations;       // metadata.annotations
    private String namespace;                      // metadata.namespace

    // DeploymentSpec
    private Integer replicas = 1;                  // spec.replicas
    private Map<String, String> selector;          // spec.selector.matchLabels
    private List<Container> containers;            // spec.template.spec.containers
    private Object volumes;                        // spec.template.spec.volumes
    private String strategyType;                   // spec.strategy.type (Recreate, RollingUpdate)
    private Map<String, String> templateLabels;    // spec.template.metadata.labels
    private Map<String, String> templateAnnotations; // spec.template.metadata.annotations
    private String restartPolicy;                  // spec.template.spec.restartPolicy
    private String serviceAccountName;             // spec.template.spec.serviceAccountName
    private List<String> imagePullSecrets;         // spec.template.spec.imagePullSecrets

    // DeploymentStatus (可选)
    private Integer availableReplicas;             // status.availableReplicas
    private Integer readyReplicas;                 // status.readyReplicas

    public Deployment(String name, List<Container> containers) {
        this.name = name;
        this.containers = containers;
    }

    public static class Builder {
        private final Deployment deployment;

        public Builder(String name, List<Container> containers) {
            deployment = new Deployment(name, containers);
        }

        public Builder replicas(Integer replicas) {
            deployment.replicas = replicas;
            return this;
        }

        public Builder volumes(List<Volume> volumes) {
            deployment.volumes = volumes;
            return this;
        }

        public Builder namespace(String namespace) {
            deployment.namespace = namespace;
            return this;
        }

        public Builder labels(Map<String, String> labels) {
            deployment.labels = labels;
            return this;
        }

        public Builder annotations(Map<String, String> annotations) {
            deployment.annotations = annotations;
            return this;
        }

        public Builder selector(Map<String, String> selector) {
            deployment.selector = selector;
            return this;
        }

        public Builder strategyType(String strategyType) {
            deployment.strategyType = strategyType;
            return this;
        }

        public Builder templateLabels(Map<String, String> templateLabels) {
            deployment.templateLabels = templateLabels;
            return this;
        }

        public Builder templateAnnotations(Map<String, String> templateAnnotations) {
            deployment.templateAnnotations = templateAnnotations;
            return this;
        }

        public Builder restartPolicy(String restartPolicy) {
            deployment.restartPolicy = restartPolicy;
            return this;
        }

        public Builder serviceAccountName(String serviceAccountName) {
            deployment.serviceAccountName = serviceAccountName;
            return this;
        }

        public Builder imagePullSecrets(List<String> imagePullSecrets) {
            deployment.imagePullSecrets = imagePullSecrets;
            return this;
        }

        public Deployment build() {
            return deployment;
        }

    }
}
