package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Probe {
    // Getters and Setters
    private Handler handler;
    private Integer initialDelaySeconds;
    private Integer timeoutSeconds;
    private Integer periodSeconds;
    private Integer successThreshold;
    private Integer failureThreshold;

}
