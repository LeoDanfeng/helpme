package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VolumeMount {
    // Getters and Setters
    private String name;
    private String mountPath;
    private Boolean readOnly;
    private String subPath;

    public VolumeMount(String name, String mountPath) {
        this.name = name;
        this.mountPath = mountPath;
    }

}
