package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContainerPort {
    // Getters and Setters
    private String name;
    private Integer containerPort;
    private String protocol;
    private Integer hostPort;
    private String hostIP;

    public ContainerPort(Integer containerPort) {
        this.containerPort = containerPort;
    }

    public ContainerPort(String name, Integer containerPort) {
        this.name = name;
        this.containerPort = containerPort;
    }

}
