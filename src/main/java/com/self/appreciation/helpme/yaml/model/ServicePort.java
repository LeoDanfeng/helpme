package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServicePort {

    private String name;                       // port.name
    private String protocol = "TCP";           // port.protocol (TCP, UDP, SCTP)
    private Integer port;                      // port.port
    private Integer targetPort;                 // port.targetPort
    private Integer nodePort;                  // port.nodePort (for NodePort service)


    public ServicePort(Integer port, Integer targetPort) {
        this.port = port;
        this.targetPort = targetPort;
    }
}
