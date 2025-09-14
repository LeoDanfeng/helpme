package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersistentVolumeClaimVolumeSource {
    // Getters and Setters
    private String claimName;
    private Boolean readOnly;

    public PersistentVolumeClaimVolumeSource(String claimName) {
        this.claimName = claimName;
    }

    public PersistentVolumeClaimVolumeSource(String claimName, Boolean readOnly) {
        this.claimName = claimName;
        this.readOnly = readOnly;
    }

}
