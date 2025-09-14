package com.self.appreciation.helpme.yaml.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SecretVolumeSource {
    // Getters and Setters
    private String secretName;
    private Boolean optional;

}
