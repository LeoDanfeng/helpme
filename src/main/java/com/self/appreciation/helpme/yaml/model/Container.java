package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
public class Container {
    private String name;
    private String image;
    private String imagePullPolicy;
    private List<ContainerPort> ports;
    private List<EnvVar> env;
    private ResourceRequirements resources;
    private List<VolumeMount> volumeMounts;
    private Probe livenessProbe;
    private Probe readinessProbe;
    private List<String> command;
    private List<String> args;

    public Container(String name, String image) {
        this.name = name;
        this.image = image;
        imagePullPolicy = "IfNotPresent";
    }


    // Builder pattern
    public static class Builder {
        private final Container container;

        public Builder() {
            this.container = new Container();
        }

        public Builder(String name, String image) {
            this.container = new Container(name, image);
        }

        public Builder name(String name) {
            container.setName(name);
            return this;
        }

        public Builder image(String image) {
            container.setImage(image);
            return this;
        }

        public Builder imagePullPolicy(String imagePullPolicy) {
            container.setImagePullPolicy(imagePullPolicy);
            return this;
        }

        public Builder ports(List<ContainerPort> ports) {
            container.setPorts(ports);
            return this;
        }

        public Builder env(List<EnvVar> env) {
            container.setEnv(env);
            return this;
        }

        public Builder resources(ResourceRequirements resources) {
            container.setResources(resources);
            return this;
        }

        public Builder volumeMounts(List<VolumeMount> volumeMounts) {
            container.setVolumeMounts(volumeMounts);
            return this;
        }

        public Builder livenessProbe(Probe livenessProbe) {
            container.setLivenessProbe(livenessProbe);
            return this;
        }

        public Builder readinessProbe(Probe readinessProbe) {
            container.setReadinessProbe(readinessProbe);
            return this;
        }

        public Builder command(List<String> command) {
            container.setCommand(command);
            return this;
        }

        public Builder args(List<String> args) {
            container.setArgs(args);
            return this;
        }

        public Container build() {
            return container;
        }
    }
}
