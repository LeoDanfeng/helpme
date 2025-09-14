package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HostPathVolumeSource {
    // Getters and Setters
    private String path;
    private String type;

}
