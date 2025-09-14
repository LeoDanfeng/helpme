package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class Service {

    // ObjectMeta
    private String name;                     // metadata.name
    private String namespace;                      // metadata.namespace
    private Map<String, String> labels;            // metadata.labels
    private Map<String, String> annotations;       // metadata.annotations

    // ServiceSpec
    private List<ServicePort> ports;                // spec.ports
    private String type = "ClusterIP";             // spec.type (ClusterIP, NodePort, LoadBalancer, ExternalName)
    private String clusterIP;                      // spec.clusterIP
    private Map<String, String> selector;          // spec.selector
    private List<String> externalIPs;              // spec.externalIPs
    private String externalName;                   // spec.externalName
    private List<Integer> externalTrafficPolicy;   // spec.externalTrafficPolicy
    private Integer healthCheckNodePort;           // spec.healthCheckNodePort
    private List<String> loadBalancerIP;           // spec.loadBalancerIP
    private List<String> loadBalancerSourceRanges; // spec.loadBalancerSourceRanges
    private String sessionAffinity;                // spec.sessionAffinity
    private Map<String, String> sessionAffinityConfig; // spec.sessionAffinityConfig

    public Service(String name, Map<String, String> selector, List<ServicePort> ports) {
        this.name = name;
        this.selector = selector;
        this.ports = ports;
    }

    // ServiceStatus (可选)
    private Map<String, Object> loadBalancer;      // status.loadBalancer
}
