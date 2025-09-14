package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Volume {
    private String name;
    private PersistentVolumeClaimVolumeSource persistentVolumeClaim;
    private ConfigMapVolumeSource configMap;
    private SecretVolumeSource secret;
    private EmptyDirVolumeSource emptyDir;
    private HostPathVolumeSource hostPath;

    public Volume(String name) {
        this.name = name;
    }

}
